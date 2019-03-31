package com.bateleur.app.controller;

import java.util.Iterator;

import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.QueueModel;
import com.bateleur.app.model.SettingsModel;
import com.bateleur.app.view.BBackgroundCanvas;
import com.therealergo.main.math.Vector3D;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.Light;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

public class PlaybackController {

    enum RepeatSetting {
        REPEAT_OFF,
        REPEAT_QUEUE,
        REPEAT_ONE
    }
    
    @FXML
    private BBackgroundCanvas backgroundCanvas;
    
    @FXML
    private GridPane lowerPane;
    
    @FXML
    private AnchorPane root;
    
    @FXML
    private AnchorPane musicSelectPane;
    
    @FXML
    private AnchorPane playbackBar;
    
    @FXML
    private AnchorPane playbackBarLeft;
    
    @FXML
    private AnchorPane topBar;
    
    @FXML
    private AnchorPane topBarBG;
    
    @FXML
    private AnchorPane topBarFG;

    @FXML
    private Label textBot;

    @FXML
    private Label textTop;
    
    @FXML
    private ImageView playbackImage;
    
    @FXML
    private ColumnConstraints playbackImageContainer;
    
    @FXML
    private Label topBarLabel;

    @FXML
    private Button skipBackwardButton;

    @FXML
    private Button skipForwardButton;

    @FXML
    private ToggleButton playPauseButton;

    @FXML
    private ToggleButton shuffleButton;

    @FXML
    private ToggleButton queueButton;

    @FXML
    private ToggleButton repeatButton;

    @FXML
    private ImageView playPauseButtonImage_O;

    @FXML
    private ImageView shuffleButtonImage_O;

    @FXML
    private ImageView queueButtonImage_O;

    @FXML
    private ImageView repeatButtonImage_O;

    @FXML
    private ImageView playPauseButtonImage_I;

    @FXML
    private ImageView shuffleButtonImage_I;

    @FXML
    private ImageView queueButtonImage_I;

    @FXML
    private ImageView repeatButtonImage_I;

    @FXML
    private Slider seekBar;

    @FXML
    private Slider volumeBar;

    // Regular fields

    private SettingsModel settings;

    private PlaybackModel playback;

    private QueueModel queue;

    private boolean TEMP_OSS = false;
    
    private Timeline slideAnimation;
    private BoxBlur blurEffect;
    Lighting lightingFG;
    Lighting lightingBG;
    Lighting lightingBO;

    public PlaybackController(SettingsModel settings, PlaybackModel playback, QueueModel queue) {
        this.settings = settings;
        this.playback = playback;
        this.queue = queue;
    }
    
    private double smoothstep(double x) {
		x = x * x * (3 - 2 * x);
		x = x * x * (3 - 2 * x);
		x = x * x * (3 - 2 * x);
		return x;
	}
    
    @FXML
    public void initialize() {
    	shuffleButton.setSelected(queue.isShuffleEnabled());
    	shuffleButtonImage_O.setOpacity(shuffleButton.isSelected() ? 1.0 : 0.0);
    	shuffleButtonImage_I.setOpacity(shuffleButton.isSelected() ? 0.0 : 1.0);

    	queueButton  .setSelected(queue.isQueueEnabled()  );
    	queueButtonImage_O  .setOpacity(queueButton  .isSelected() ? 1.0 : 0.0);
    	queueButtonImage_I  .setOpacity(queueButton  .isSelected() ? 0.0 : 1.0);

    	repeatButton .setSelected(queue.isRepeatEnabled() );
    	repeatButtonImage_O .setOpacity(repeatButton .isSelected() ? 1.0 : 0.0);
    	repeatButtonImage_I .setOpacity(repeatButton .isSelected() ? 0.0 : 1.0);
        
        playback.addPlayHandler(() -> {
	    	playPauseButtonImage_O.setOpacity(0.0);
	    	playPauseButtonImage_I.setOpacity(1.0);
        });
        playback.addPauseHandler(() -> {
	    	playPauseButtonImage_O.setOpacity(1.0);
	    	playPauseButtonImage_I.setOpacity(0.0);
        });
    	
    	volumeBar.setMin(0.0);
    	volumeBar.setMax(100.0);
    	volumeBar.setValue(playback.getVolume()*100.0);
    	volumeBar.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
    		onVolumeSet(new_val.doubleValue()/100.0);
        });
    	
    	seekBar.setMin(0.0);
    	seekBar.setMax(1.0);
    	seekBar.setValue(0.0);
    	seekBar.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
    		if (TEMP_OSS == false) {
        		onSeekSet(new_val.doubleValue());
    		}
        });
		
        lightingBG = new Lighting();
        lightingBG.setDiffuseConstant(1.0);
        lightingBG.setSpecularConstant(0.0);
        lightingBG.setSpecularExponent(0.0);
        lightingBG.setSurfaceScale(0.0);
        lightingBG.setLight(new Light.Distant(0, 90.0, Color.BLACK));
    	Iterator<Node> BGNodes = root.lookupAll("#colorBG").iterator();
    	while (BGNodes.hasNext()) {
    		BGNodes.next().setEffect(lightingBG);
    	}
		
        lightingFG = new Lighting();
        lightingFG.setDiffuseConstant(1.0);
        lightingFG.setSpecularConstant(0.0);
        lightingFG.setSpecularExponent(0.0);
        lightingFG.setSurfaceScale(0.0);
        lightingFG.setLight(new Light.Distant(0, 90.0, Color.WHITE));
    	Iterator<Node> FGNodes = root.lookupAll("#colorFG").iterator();
    	while (FGNodes.hasNext()) {
    		FGNodes.next().setEffect(lightingFG);
    	}
		
        lightingBO = new Lighting();
        lightingBO.setDiffuseConstant(1.0);
        lightingBO.setSpecularConstant(0.0);
        lightingBO.setSpecularExponent(0.0);
        lightingBO.setSurfaceScale(0.0);
        lightingBO.setLight(new Light.Distant(0, 90.0, Color.WHITE));
    	Iterator<Node> BONodes = root.lookupAll("#colorBO").iterator();
    	while (BONodes.hasNext()) {
    		BONodes.next().setEffect(lightingBO);
    	}
    	
    	topBarLabel.setTranslateX(1.0);
    	
        updateText();
        
        Thread javaFXThread = Thread.currentThread();
        new Thread() {
        	public void run() {
        		while (!javaFXThread.getState().equals(State.TERMINATED)) {
            		try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                    Platform.runLater(new Runnable() {
						@Override  public void run() {
                        	TEMP_OSS = true;
                    		seekBar.setValue(playback.getPlaybackTimeMS() / playback.getPlaybackLengthMS());
                        	TEMP_OSS = false;
                        	if (playback.getPlaybackTimeMS() >= playback.getPlaybackLengthMS()) {
                            	queue.skipForwards();
                                playback.loadAudio(queue.get(), settings.get(settings.FADE_TIME_USER));
                                playback.play(settings.get(settings.FADE_TIME_USER));
                                updateText();
                        	}
                        }
                    });
        		}
        	}
        }.start();
        
        blurEffect = new BoxBlur(0, 0, 3);
        lowerPane.setEffect(blurEffect);
        
        ChangeListener<Number> cl = new ChangeListener<Number>() {
            @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) {
            	if (lowerPane.getTranslateY() != 0) {
                	lowerPane.setTranslateY(musicSelectPane.getHeight());
            	}
            	if (playbackBarLeft.getTranslateX() != 0) {
            		playbackBarLeft.setTranslateX(-playbackImageContainer.getMinWidth());
            	}
            	if (topBarLabel.getTranslateX() != 0) {
            		topBarLabel.setTranslateX(playbackImageContainer.getMinWidth());
            	}
            	
            	double   rate = 0.0 ;
            	Duration time = null;
            	if (slideAnimation != null) {
                	rate = slideAnimation.getRate();
                	time = slideAnimation.getCurrentTime();
            	}
            	
                slideAnimation = new Timeline();
                slideAnimation.setRate(-1.0);
                for (int i = 0; i<100; i++) {
                	double pct0 = smoothstep((i-1)/99.0);
                	double pct1 = smoothstep((i  )/99.0);
                	double pct2 = smoothstep((i+1)/99.0);
                    slideAnimation.getKeyFrames().add(
            	        	new KeyFrame(new Duration(i*settings.get(settings.UI_ANIM_TIME_MUL)),
            	        			new KeyValue(topBarLabel.translateXProperty(), (1.0-pct1)*playbackImageContainer.getMinWidth()), 
            	        			new KeyValue(playbackBarLeft.translateXProperty(), -pct1*playbackImageContainer.getMinWidth()), 
            	        			new KeyValue(blurEffect.heightProperty(), (pct2-pct0)*settings.get(settings.UI_MOTION_BLUR_MUL)*musicSelectPane.getHeight()), 
            	        			new KeyValue(lowerPane.translateYProperty(), pct1*musicSelectPane.getHeight()), 
            	        			new KeyValue(backgroundCanvas.artAlpha, pct1)
            	            )
                    );
                }
                
            	if (time != null) {
	                slideAnimation.setRate(rate);
	                slideAnimation.jumpTo (time);
            	}
            }
        };
        musicSelectPane  .heightProperty()  .addListener(cl);
        playbackImageContainer.minWidthProperty().addListener(cl);
    }
    
    private void updateText() {
    	textTop.setText(playback.getLoadedAudio().get(settings.AUDIO_PROP_ARTIST));
    	textBot.setText(playback.getLoadedAudio().get(settings.AUDIO_PROP_TITLE));
    	try {
    		Image im = playback.getLoadedAudio().get(settings.AUDIO_PROP_ART).getImageThumbnail();
    		playbackImageContainer.setMinWidth(im.getWidth() / im.getHeight() * 107.0);
			playbackImage.setImage(im);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Vector3D cBG_VEC = playback.getLoadedAudio().get(settings.AUDIO_PROP_COLR_BG);
		Vector3D cFG_VEC = playback.getLoadedAudio().get(settings.AUDIO_PROP_COLR_FG);
		Color cBG = new Color(cBG_VEC.x, cBG_VEC.y, cBG_VEC.z, 1.0);
		Color cFG = new Color(cFG_VEC.x, cFG_VEC.y, cFG_VEC.z, 1.0);
		lightingBG.getLight().setColor(cBG);
		lightingFG.getLight().setColor(cFG);
		lightingBO.getLight().setColor(cBG.interpolate(cFG, 0.3));
    }

    @FXML
    public void onShufflePress() {
    	queue.setShuffleState(shuffleButton.isSelected());
    	shuffleButtonImage_O.setOpacity(shuffleButton.isSelected() ? 1.0 : 0.0);
    	shuffleButtonImage_I.setOpacity(shuffleButton.isSelected() ? 0.0 : 1.0);
    }

    @FXML
    public void onQueuePress() {
    	queue.setQueueState(queueButton.isSelected());
    	queueButtonImage_O.setOpacity(queueButton.isSelected() ? 1.0 : 0.0);
    	queueButtonImage_I.setOpacity(queueButton.isSelected() ? 0.0 : 1.0);
    }

    @FXML
    public void onRepeatPress() throws Exception {
    	queue.setRepeatState(repeatButton.isSelected());
    	repeatButtonImage_O.setOpacity(repeatButton.isSelected() ? 1.0 : 0.0);
    	repeatButtonImage_I.setOpacity(repeatButton.isSelected() ? 0.0 : 1.0);
    }

    @FXML
    public void onBarPress() throws Exception {
    	slideAnimation.setRate(-slideAnimation.getRate());
    	slideAnimation.play();
    }

    @FXML
    public void onPlayPausePress() {
        if (playback.isPlaying()) {
            playback.pause(settings.get(settings.FADE_TIME_USER));
        } else {
            playback.play(settings.get(settings.FADE_TIME_USER));
        }
    }

    @FXML
    public void onSkipForwardPress() {
    	queue.skipForwards();
        playback.loadAudio(queue.get(), settings.get(settings.FADE_TIME_USER));
        playback.play(settings.get(settings.FADE_TIME_USER));
        updateText();
    }

    @FXML
    public void onSkipBackwardPress() {
    	queue.skipBackwards();
        playback.loadAudio(queue.get(), settings.get(settings.FADE_TIME_USER));
        playback.play(settings.get(settings.FADE_TIME_USER));
        updateText();
    }

    public void onPlayTimeIncrease() {
    }
    
    public void onVolumeSet(double volume) {
    	playback.setVolume(volume);
    }

    public void onSeekSet(double seek) {
    	playback.setPlaybackTimeMS(playback.getPlaybackLengthMS() * seek);
    	playback.play(settings.get(settings.FADE_TIME_USER));
    }
}
