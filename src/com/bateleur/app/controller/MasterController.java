package com.bateleur.app.controller;

import java.util.ArrayList;
import java.util.List;

import com.bateleur.app.model.LibraryModel;
import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.QueueModel;
import com.bateleur.app.model.SettingsModel;
import com.bateleur.app.view.BBackgroundCanvas;
import com.therealergo.main.NilConsumer;
import com.therealergo.main.NilEvent;
import com.therealergo.main.math.Vector3D;

import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class MasterController {
	/** FXML-injected component references. */
	@FXML private BBackgroundCanvas backgroundCanvas;
	@FXML private AnchorPane        root            ;
	@FXML private AnchorPane        topBar          ;
	@FXML private AnchorPane        topBarBG        ;
	@FXML private AnchorPane        topBarFG        ;
	@FXML private AnchorPane        topBarBO        ;
	@FXML private AnchorPane        canvasBorderBO  ;
	@FXML public  Label             topBarLabel     ;
	@FXML public  GridPane          lowerPane       ;
	
	/** FXML-injected sub-controller references */
	@FXML private PlaybackController  playbackController ;
	@FXML private MusicListController musicListController;
	
	/** Reference to the SettingsModel that manages all of the application settings. */
	public final SettingsModel settings;
	/** Reference to the SettingsModel that manages application playback. */
	public final PlaybackModel playback;
	/** Reference to the LibraryModel that manages the application's audio library. */
	public final LibraryModel  library ;
	/** Reference to the SettingsModel that manages application queue state. */
	public final QueueModel    queue   ;
	
	/** PlaybackColorAnimation instance that is used to colorize the window and fade between different window color settings. */
	public final PlaybackColorAnimation playbackColorAnimation;
	/** VerticalSlideAnimation instance that is used to animate the playback bar at the bottom of the window sliding up/down. */
	public final VerticalSlideAnimation verticalSlideAnimation;
	
	/**
	 * Constructor for MasterController that supplies Model instances for it to refer to, 
	 * should be called by a custom builder that provides each instance to the generated MasterController instance.
	 * @param settings The SettingsModel that this MasterController will use.
	 * @param playback The PlaybackModel that this MasterController will use.
	 * @param library  The LibraryModel that this MasterController will use.
	 * @param queue    The QueueModel that this MasterController will use.
	 */
	public MasterController(SettingsModel settings, PlaybackModel playback, LibraryModel library, QueueModel queue) {
		this.settings = settings;
		this.playback = playback;
		this.library  = library ;
		this.queue    = queue   ;
		
		this.playbackColorAnimation = new PlaybackColorAnimation();
		this.verticalSlideAnimation = new VerticalSlideAnimation();
	}
	
	/**
	 * FXML callback that initializes this MasterController after it has been created and FXML startup is complete.
	 */
	@FXML public void initialize() {
		// Build the playback color animation
		{
			topBarFG.setEffect(playbackColorAnimation.lightingFG);
			topBarBG.setEffect(playbackColorAnimation.lightingBG);
			canvasBorderBO.setEffect(playbackColorAnimation.lightingBO);
		}
		
		// Set UI values looked up from settings
		{
			root.setStyle(
				settings.UI_TITLEBAR_VSIZE.key + ": " + settings.UI_TITLEBAR_VSIZE.val + ";"
			);
		}
		
		// Build the vertical slide animation
		{
			// Blur lowerPane as verticalSlideAnimation plays to make the animation appear more smooth
			BoxBlur blurEffect = new BoxBlur(0, 0, 3);
			verticalSlideAnimation.rebuildSlideAnimationEvent.addListener(() -> {
				verticalSlideAnimation.addKeyValue(
						new KeyValue(
								blurEffect.heightProperty(), 
								(verticalSlideAnimation.rebuildRate) * settings.get(settings.UI_MOTION_BLUR_MUL) * root.getHeight()
								)
						);
			});
			lowerPane.setEffect(blurEffect);
			
			// Fade backgroundCanvas.artAlpha in/out when verticalSlideAnimation plays
			verticalSlideAnimation.rebuildSlideAnimationEvent.addListener(() -> {
				verticalSlideAnimation.addKeyValue(new KeyValue(backgroundCanvas.artAlpha, verticalSlideAnimation.rebuildIndex));
			});
			
			// Rebuild the vertical slide animation every time the height of the root pane changes
			root.heightProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) -> {
				Platform.runLater(() -> {
					verticalSlideAnimation.rebuild();
				});
			});
		}
		
		// Initialize sub-controllers
		playbackController .initialize(this);
		musicListController.initialize(this);
	}
	
	/**
	 * Container for all data about rebuilding and playing the animation that causes window to fade between the current song's colors.
	 */
	public class PlaybackColorAnimation {
		/**
		 * The animation used to fade between all of the different colors.
		 * This animation simply fades 'colorPlaybackFade' between 0.0 and 1.0.
		 */
		private Timeline colorPlaybackAnimation;
		/**
		 * The property used to handle the fade-between-colors animation.
		 * The value of this property should always be between 0.0 and 1.0.
		 * When this property changes, the colors themselves, color properties, and color effects are updated to match it.
		 */
		private DoubleProperty colorPlaybackFade;
		
		/**
		 * Current BG color.
		 * This color can directly be used to style components, 
		 * but generally effect 'lightingBG' is both faster and more convenient.
		 */
		public final ObjectProperty<Color> colorPlayback_BG ;
		/** Start point of BG color animation -- when colorPlaybackFade is 0.0 colorPlayback_BG is this. */
		private      Color                 colorPlayback0_BG;
		/** End point of BG color animation -- when colorPlaybackFade is 1.0 colorPlayback_BG is this. */
		private      Color                 colorPlayback1_BG;
		
		/**
		 * Current FG color.
		 * This color can directly be used to style components, 
		 * but generally effect 'lightingFG' is both faster and more convenient.
		 */
		public final ObjectProperty<Color> colorPlayback_FG ;
		/**  Start point of FG color animation -- when colorPlaybackFade is 0.0 colorPlayback_FG is this. */
		private      Color                 colorPlayback0_FG;
		/** End point of FG color animation -- when colorPlaybackFade is 1.0 colorPlayback_FG is this. */
		private      Color                 colorPlayback1_FG;
		
		/**
		 * Current BO color.
		 * This color can directly be used to style components, 
		 * but generally effect 'lightingBO' is both faster and more convenient.
		 */
		public final ObjectProperty<Color> colorPlayback_BO ;
		/**  Start point of BO color animation -- when colorPlaybackFade is 0.0 colorPlayback_BO is this. */
		private      Color                 colorPlayback0_BO;
		/** End point of BO color animation -- when colorPlaybackFade is 1.0 colorPlayback_BO is this. */
		private      Color                 colorPlayback1_BO;
		
		/**
		 * Current LI color.
		 * This color can directly be used to style components, 
		 * but generally effect 'lightingLI' is both faster and more convenient.
		 */
		public final ObjectProperty<Color> colorPlayback_LI ;
		/**  Start point of LI color animation -- when colorPlaybackFade is 0.0 colorPlayback_LI is this. */
		private      Color                 colorPlayback0_LI;
		/** End point of LI color animation -- when colorPlaybackFade is 1.0 colorPlayback_LI is this. */
		private      Color                 colorPlayback1_LI;
		
		/**
		 * Effect that is applied to multiply a component by the BG-color.
		 */
		public final Lighting lightingBG;
		/**
		 * Effect that is applied to multiply a component by the FG-color.
		 */
		public final Lighting lightingFG;
		/**
		 * Effect that is applied to multiply a component by the BO-color.
		 */
		public final Lighting lightingBO;
		/**
		 * Effect that is applied to multiply a component by the LI-color.
		 */
		public final Lighting lightingLI;
		
		/**
		 * Constructor for PlaybackColorAnimation.
		 * Should be used only by MasterController to create its internal PlaybackColorAnimation instance.
		 */
		public PlaybackColorAnimation() {
			// Create color properties
			this.colorPlayback_BG = new SimpleObjectProperty<Color>();
			this.colorPlayback_FG = new SimpleObjectProperty<Color>();
			this.colorPlayback_BO = new SimpleObjectProperty<Color>();
			this.colorPlayback_LI = new SimpleObjectProperty<Color>();
			
			// Create each lighting effect
			// These lighting effects are setup to just multiply by their light color
			{
				this.lightingBG = new Lighting();
				lightingBG.setDiffuseConstant(1.0);
				lightingBG.setSpecularConstant(0.0);
				lightingBG.setSpecularExponent(0.0);
				lightingBG.setSurfaceScale(0.0);
				lightingBG.setLight(new Light.Distant(0, 90.0, Color.BLACK));
				
				this.lightingFG = new Lighting();
				lightingFG.setDiffuseConstant(1.0);
				lightingFG.setSpecularConstant(0.0);
				lightingFG.setSpecularExponent(0.0);
				lightingFG.setSurfaceScale(0.0);
				lightingFG.setLight(new Light.Distant(0, 90.0, Color.WHITE));
				
				this.lightingBO = new Lighting();
				lightingBO.setDiffuseConstant(1.0);
				lightingBO.setSpecularConstant(0.0);
				lightingBO.setSpecularExponent(0.0);
				lightingBO.setSurfaceScale(0.0);
				lightingBO.setLight(new Light.Distant(0, 90.0, Color.WHITE));
				
				this.lightingLI = new Lighting();
				lightingLI.setDiffuseConstant(1.0);
				lightingLI.setSpecularConstant(0.0);
				lightingLI.setSpecularExponent(0.0);
				lightingLI.setSurfaceScale(0.0);
				lightingLI.setLight(new Light.Distant(0, 90.0, Color.WHITE));
			}
			
			// Create the property used to handle the fade-between-colors animation
			colorPlaybackFade = new SimpleDoubleProperty();
			colorPlaybackFade.addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observableValue, Number oldIndex, Number newIndex) {
					// Compute the new colors by interpolating between the start- and end-points
					double index = newIndex.doubleValue();
					colorPlayback_BG.set(colorPlayback0_BG.interpolate(colorPlayback1_BG, index));
					colorPlayback_FG.set(colorPlayback0_FG.interpolate(colorPlayback1_FG, index));
					colorPlayback_BO.set(colorPlayback0_BO.interpolate(colorPlayback1_BO, index));
					colorPlayback_LI.set(colorPlayback0_LI.interpolate(colorPlayback1_LI, index));
					
					// Set the lighting effect colors to the newly-computed colors
					lightingBG.setLight(new Light.Distant(0, 90.0, colorPlayback_BG.get()));
					lightingFG.setLight(new Light.Distant(0, 90.0, colorPlayback_FG.get()));
					lightingBO.setLight(new Light.Distant(0, 90.0, colorPlayback_BO.get()));
					lightingLI.setLight(new Light.Distant(0, 90.0, colorPlayback_LI.get()));
				}
			});
			
			// Rebuild all of the colors and start the color-fade animation when the song changes
			playback.addSongChangeHandler(() -> {
				// Set the color animation endpoints to the computed BG, FG, BO, and LI colors
				Vector3D cBG_VEC = library.getByReference(playback.getLoadedAudio()).get(settings.AUDIO_PROP_COLR_BG);
				Vector3D cFG_VEC = library.getByReference(playback.getLoadedAudio()).get(settings.AUDIO_PROP_COLR_FG);
				colorPlayback1_BG = new Color(cBG_VEC.x, cBG_VEC.y, cBG_VEC.z, 1.0);
				colorPlayback1_FG = new Color(cFG_VEC.x, cFG_VEC.y, cFG_VEC.z, 1.0);
				colorPlayback1_BO = colorPlayback1_BG.interpolate(colorPlayback1_FG, 0.3);
				colorPlayback1_LI = colorPlayback1_BG.interpolate(colorPlayback1_FG, 0.1);
				
				// If the animation was already created and playing, we need to set the color-fade startpoint to the current value
				// Otherwise, we just start at the endpoint, as we have no color to fade 'from'
				if (colorPlaybackAnimation == null) {
					colorPlayback0_BG = colorPlayback1_BG;
					colorPlayback0_FG = colorPlayback1_FG;
					colorPlayback0_BO = colorPlayback1_BO;
					colorPlayback0_LI = colorPlayback1_LI;
				} else {
					colorPlaybackAnimation.stop();
					colorPlayback0_BG = colorPlayback_BG.get();
					colorPlayback0_FG = colorPlayback_FG.get();
					colorPlayback0_BO = colorPlayback_BO.get();
					colorPlayback0_LI = colorPlayback_LI.get();
				}
				
				// Recreate the fade animation starting at colorPlaybackFade==0.0 and begin playback
				double playTime = settings.get(settings.UI_ANIM_TIME_MUL)*0.2;
				colorPlaybackAnimation = new Timeline(
					new KeyFrame(Duration.seconds(0       ), new KeyValue(colorPlaybackFade, 0.0, Interpolator.EASE_BOTH)),
					new KeyFrame(Duration.seconds(playTime), new KeyValue(colorPlaybackFade, 1.0, Interpolator.EASE_BOTH))
				);
				colorPlaybackAnimation.play();
			});
		}
	}
	
	/**
	 * Container for all data about rebuilding and playing the animation that causes the lower list to slide up and down.
	 */
	public class VerticalSlideAnimation {
		/**
		 * Event called whenever a new KeyFrame needs to be built by this VerticalSlideAnimation.
		 */
		private final NilEvent rebuildSlideAnimationEvent;
		
		/**
		 * List of KeyValues currently created as a part of building this KeyFrame.
		 */
		private final List<KeyValue> buildValues;
		/**
		 * Boolean set to true while this VerticalSlideAnimation is being rebuilt and false otherwise.
		 */
		private boolean isRebuilding;
		/**
		 * The Timeline instance containing this VerticalSlideAnimation's actual animation.
		 */
		private Timeline slideAnimation;
		/**
		 * The index within the animation, from 0.0 to 1.0, at the currently-being-built KeyFrame.
		 */
		private double rebuildIndex;
		/**
		 * The rate of the animation, a value >=0.0, at the currently-being-built KeyFrame.
		 */
		private double rebuildRate;
		
		/**
		 * Constructor for VerticalSlideAnimation.
		 * Should be used only by MasterController to create its internal VerticalSlideAnimation instance.
		 */
		public VerticalSlideAnimation() {
			this.rebuildSlideAnimationEvent = new NilEvent();
			
			this.buildValues = new ArrayList<KeyValue>();
			this.isRebuilding = false;
		}
		
		/**
		 * Internal implementation of the smoothstep() algorithm called repeatedly 3 times on the input value.
		 * The smoothstep function has derivatives of 0 and x=0.0 and x=1.0, giving a 'smoother' and 'more natural' interpolation.
		 * By applying this function three times, the animation will also be much slower towards its beginning and end and quicker towards its middle.
		 * @param x The input smoothstep interpolator, which should be between 0.0 and 1.0.
		 * @return The resulting value, which is between 0.0 and 1.0.
		 */
		private double smoothstep3(double x) {
			x = x * x * (3 - 2 * x);
			x = x * x * (3 - 2 * x);
			x = x * x * (3 - 2 * x);
			return x;
		}
		
		/**
		 * Rebuild this VerticalSlideAnimation.
		 * This needs to be called whenever one of this VerticalSlideAnimation's KeyValues is to be regenerated.
		 * For example, if a KeyValue uses the value of another component's height, 
		 * this method must be called whenever that component's height changes.
		 * @throws This VerticalSlideAnimation must currently not be in the process of being rebuilt.
		 */
		public void rebuild() {
			assert(!isRebuilding);
			
			// Signal that we are rebuilding
			isRebuilding = true;
			
			// Save the initial rate/time of the animation, if it has already been built
			double   rate = 0.0 ;
			Duration time = null;
			if (slideAnimation != null) {
				rate = slideAnimation.getRate();
				time = slideAnimation.getCurrentTime();
			}
			
			// Create a new animation Timeline instance
			slideAnimation = new Timeline();
			slideAnimation.setRate(-1.0);
			
			// Add 100 KeyFrames to the Timeline instance
			for (int i = 0; i<100; i++) {
				// Compute the index and rate for this KeyFrame
				rebuildIndex = smoothstep3((i  )/99.0);
				rebuildRate  = smoothstep3((i+1)/99.0) - smoothstep3((i-1)/99.0);
				
				// Regenerate the list of KeyValues used to build this KeyFrame
				buildValues.clear();
				rebuildSlideAnimationEvent.accept();
				
				// Add a KeyFrame made up of the generated list of KeyValues
				slideAnimation.getKeyFrames().add(
					new KeyFrame(
						new Duration(i*settings.get(settings.UI_ANIM_TIME_MUL)),
						buildValues.toArray(new KeyValue[buildValues.size()])
					)
				);
			}
			
			// Set the new animation to the initial animation's rate/time if applicable
			if (time != null) {
				slideAnimation.setRate(rate);
				slideAnimation.jumpTo (time);
				slideAnimation.play();
			}
			
			// Signal that rebuilding has completed
			isRebuilding = false;
		}
		
		/**
		 * Add an action to be performed while rebuilding this VerticalSlideAnimation.
		 * This action is called whenever a new KeyValue needs to be added for this VerticalSlideAnimation's current animation index.
		 * Typically, it add a KeyValue that changes some parameter based upon this VerticalSlideAnimation's rebuildIndex() and rebuildRate() values.
		 * @param rebuildAction The action to be performed as this VerticalSlideAnimation is rebuilt.
		 * @throws This VerticalSlideAnimation must currently not be in the process of being rebuilt.
		 */
		public void onRebuild(NilConsumer rebuildAction) {
			assert(!isRebuilding);
			
			rebuildSlideAnimationEvent.addListener(rebuildAction);
		}
		
		/**
		 * Adds the given KeyValue to this VerticalSlideAnimation.
		 * A VerticalSlideAnimation is made up of many KeyValues, 
		 * each of which must be added using this method while the VerticalSlideAnimation is being rebuilt.
		 * @param val The KeyValue to add to this VerticalSlideAnimation.
		 * @throws This VerticalSlideAnimation must currently be in the process of being rebuilt.
		 */
		public void addKeyValue(KeyValue val) {
			assert(isRebuilding);
			
			buildValues.add(val);
		}
		
		/**
		 * @return The animation index in range 0.0 - 1.0 at the currently-being-built KeyFrame.
		 * @throws This VerticalSlideAnimation must currently be in the process of being rebuilt.
		 */
		public double rebuildIndex() {
			assert(isRebuilding);
			
			return rebuildIndex;
		}
		
		/**
		 * @return The animation rate (>=0.0) at the currently-being-built KeyFrame.
		 * @throws This VerticalSlideAnimation must currently be in the process of being rebuilt.
		 */
		public double rebuildRate() {
			assert(isRebuilding);
			
			return rebuildRate;
		}
		
		/**
		 * Play this VerticalSlideAnimation.
		 * This will cause the animation to play, toggling between sliding upwards and downwards motion on every call.
		 * @throws This VerticalSlideAnimation must currently not be in the process of being rebuilt.
		 */
		public void play() {
			assert(!isRebuilding);
			
			// Rebuild animation if it has not yet been built
			if (slideAnimation == null) {
				rebuild();
			}
			
			// Flip the animation's direction and play it
			slideAnimation.setRate(-slideAnimation.getRate());
			slideAnimation.play();
		}
	}
}
