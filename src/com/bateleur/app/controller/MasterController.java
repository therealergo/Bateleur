package com.bateleur.app.controller;

import java.util.ArrayList;
import java.util.List;

import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.QueueModel;
import com.bateleur.app.model.SettingsModel;
import com.bateleur.app.view.BBackgroundCanvas;
import com.therealergo.main.NilConsumer;
import com.therealergo.main.NilEvent;
import com.therealergo.main.math.Vector3D;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
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
    @FXML private BBackgroundCanvas backgroundCanvas;
    @FXML private AnchorPane        root            ;
    @FXML private AnchorPane        topBar          ;
    @FXML private AnchorPane        topBarBG        ;
    @FXML private AnchorPane        topBarFG        ;
    @FXML private AnchorPane        topBarBO        ;
    @FXML private AnchorPane        canvasBorderBO  ;
    @FXML public  Label             topBarLabel     ;
	@FXML public  GridPane          lowerPane       ;
    
    @FXML private PlaybackController  playbackController ;
    @FXML private MusicListController musicListController;

    public final SettingsModel settings;
	public final PlaybackModel playback;
	public final QueueModel    queue   ;
    
    public final Lighting lightingFG;
    public final Lighting lightingBG;
    public final Lighting lightingBO;
    public final Lighting lightingLI;
	
    public final VerticalSlideAnimation verticalSlideAnimation;

    public MasterController(SettingsModel settings, PlaybackModel playback, QueueModel queue) {
    	this.settings = settings;
    	this.playback = playback;
    	this.queue    = queue   ;
    	
    	this.lightingBG = new Lighting();
    	this.lightingFG = new Lighting();
    	this.lightingBO = new Lighting();
    	this.lightingLI = new Lighting();
    	
    	this.verticalSlideAnimation = new VerticalSlideAnimation();
    }
    
    @FXML public void initialize() {
        lightingBG.setDiffuseConstant(1.0);
        lightingBG.setSpecularConstant(0.0);
        lightingBG.setSpecularExponent(0.0);
        lightingBG.setSurfaceScale(0.0);
        lightingBG.setLight(new Light.Distant(0, 90.0, Color.BLACK));
		
        lightingFG.setDiffuseConstant(1.0);
        lightingFG.setSpecularConstant(0.0);
        lightingFG.setSpecularExponent(0.0);
        lightingFG.setSurfaceScale(0.0);
        lightingFG.setLight(new Light.Distant(0, 90.0, Color.WHITE));
		
        lightingBO.setDiffuseConstant(1.0);
        lightingBO.setSpecularConstant(0.0);
        lightingBO.setSpecularExponent(0.0);
        lightingBO.setSurfaceScale(0.0);
        lightingBO.setLight(new Light.Distant(0, 90.0, Color.WHITE));
		
        lightingLI.setDiffuseConstant(1.0);
        lightingLI.setSpecularConstant(0.0);
        lightingLI.setSpecularExponent(0.0);
        lightingLI.setSurfaceScale(0.0);
        lightingLI.setLight(new Light.Distant(0, 90.0, Color.WHITE));
        
        topBarBG.setEffect(lightingBG);
        topBarFG.setEffect(lightingFG);
        topBarBO.setEffect(lightingBO);
        canvasBorderBO.setEffect(lightingBO);
        
    	topBarLabel.setTranslateX(1.0);
    	
    	playback.addSongChangeHandler(() -> {
    		Vector3D cBG_VEC = playback.getLoadedAudio().get(settings.AUDIO_PROP_COLR_BG);
    		Vector3D cFG_VEC = playback.getLoadedAudio().get(settings.AUDIO_PROP_COLR_FG);
    		Color cBG = new Color(cBG_VEC.x, cBG_VEC.y, cBG_VEC.z, 1.0);
    		Color cFG = new Color(cFG_VEC.x, cFG_VEC.y, cFG_VEC.z, 1.0);
    		lightingBG.getLight().setColor(cBG);
    		lightingFG.getLight().setColor(cFG);
    		lightingBO.getLight().setColor(cBG.interpolate(cFG, 0.3));
    		lightingLI.getLight().setColor(cBG.interpolate(cFG, 0.1));
    	});
    	
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
    	
    	playbackController .setMasterController(this);
    	playbackController .start();
    	musicListController.setMasterController(this);
    	musicListController.start();
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
