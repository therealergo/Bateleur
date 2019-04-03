package com.bateleur.app.controller;

import java.util.ArrayList;
import java.util.List;

import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.QueueModel;
import com.bateleur.app.model.SettingsModel;
import com.bateleur.app.view.BBackgroundCanvas;
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
	    				(verticalSlideAnimation.rebuildIndex_P-verticalSlideAnimation.rebuildIndex_N) * settings.get(settings.UI_MOTION_BLUR_MUL) * root.getHeight()
	    			)
				);
	    	});
	    	lowerPane.setEffect(blurEffect);
	    	
	    	// Fade backgroundCanvas.artAlpha in/out when verticalSlideAnimation plays
	    	verticalSlideAnimation.rebuildSlideAnimationEvent.addListener(() -> {
	    		verticalSlideAnimation.addKeyValue(new KeyValue(backgroundCanvas.artAlpha, verticalSlideAnimation.rebuildIndex_C));
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
    
    public class VerticalSlideAnimation {
        public final NilEvent rebuildSlideAnimationEvent;
        
        private final List<KeyValue> buildValues;
        private Timeline slideAnimation;
        public double rebuildIndex_N;
        public double rebuildIndex_C;
        public double rebuildIndex_P;
        
        public VerticalSlideAnimation() {
        	this.rebuildSlideAnimationEvent = new NilEvent();
        	
        	this.buildValues = new ArrayList<KeyValue>();
        }
    	
    	private double smoothstep(double x) {
    		x = x * x * (3 - 2 * x);
    		x = x * x * (3 - 2 * x);
    		x = x * x * (3 - 2 * x);
    		return x;
    	}
        
        public void rebuild() {
			double   rate = 0.0 ;
			Duration time = null;
			if (slideAnimation != null) {
				rate = slideAnimation.getRate();
				time = slideAnimation.getCurrentTime();
			}
			
			slideAnimation = new Timeline();
			slideAnimation.setRate(-1.0);
			for (int i = 0; i<100; i++) {
				rebuildIndex_N = smoothstep((i-1)/99.0);
				rebuildIndex_C = smoothstep((i  )/99.0);
				rebuildIndex_P = smoothstep((i+1)/99.0);
				rebuildSlideAnimationEvent.accept();
				slideAnimation.getKeyFrames().add(
						new KeyFrame(
								new Duration(i*settings.get(settings.UI_ANIM_TIME_MUL)),
								buildValues.toArray(new KeyValue[buildValues.size()])
						)
				);
				buildValues.clear();
			}
			
			if (time != null) {
				slideAnimation.setRate(rate);
				slideAnimation.jumpTo (time);
				slideAnimation.play();
			}
        }
        
        public void addKeyValue(KeyValue val) {
        	buildValues.add(val);
        }
        
        public void play() {
    		slideAnimation.setRate(-slideAnimation.getRate());
    		slideAnimation.play();
        }
    }
}
