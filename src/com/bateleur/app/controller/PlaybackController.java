package com.bateleur.app.controller;

import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.QueueModel;
import com.bateleur.app.model.SettingsModel;
import com.bateleur.app.view.BBackgroundCanvas;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.effect.BoxBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
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
    private AnchorPane musicSelectPane;
    
    @FXML
    private AnchorPane playbackPane;
    
    @FXML
    private AnchorPane playbackLeftSide;

    @FXML
    private Label textBot;

    @FXML
    private Label textTop;
    
    @FXML
    private ImageView barImage;
    
    @FXML
    private ColumnConstraints barImageContainer;

    @FXML
    private ToggleButton playPauseButton;
    
    @FXML
    private Label topBarLabel;

    @FXML
    private Button skipBackwardButton;

    @FXML
    private Button skipForwardButton;

    @FXML
    private ToggleButton shuffleButton;

    @FXML
    private ToggleButton queueButton;

    @FXML
    private ToggleButton repeatButton;

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

    	queueButton  .setSelected(queue.isQueueEnabled()  );

    	repeatButton .setSelected(queue.isRepeatEnabled() );
    	
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

        updateText();
        
        Thread javaFXThread = Thread.currentThread();
        new Thread() {
        	public void run() {
        		while (!javaFXThread.getState().equals(State.TERMINATED)) {
            		try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                    Platform.runLater(new Runnable() {
						@Override  public void run() {
                        	TEMP_OSS = true;
                    		seekBar.setValue(playback.getPlaybackTimeMS() / playback.getPlaybackLengthMS());
                        	TEMP_OSS = false;
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
            	if (playbackLeftSide.getTranslateX() != 0) {
            		playbackLeftSide.setTranslateX(-barImageContainer.getMinWidth());
            	}
            	if (topBarLabel.getTranslateX() != 0) {
            		topBarLabel.setTranslateX(barImageContainer.getMinWidth());
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
            	        			new KeyValue(topBarLabel.translateXProperty(), (1.0-pct1)*barImageContainer.getMinWidth()), 
            	        			new KeyValue(playbackLeftSide.translateXProperty(), -pct1*barImageContainer.getMinWidth()), 
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
        barImageContainer.minWidthProperty().addListener(cl);
    }
    
    private void updateText() {
    	textTop.setText(playback.getLoadedAudio().get(settings.AUDIO_PROP_TITLE));
    	textBot.setText(playback.getLoadedAudio().get(settings.AUDIO_PROP_ARTIST));
    	try {
    		Image im = playback.getLoadedAudio().get(settings.AUDIO_PROP_ART).getImage();
    		barImageContainer.setMinWidth(im.getWidth() / im.getHeight() * 107.0);
			barImage.setImage(im);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    @FXML
    public void onShufflePress() {
    	queue.setShuffleState(shuffleButton.isSelected());
    }

    @FXML
    public void onQueuePress() {
    	queue.setQueueState(queueButton.isSelected());
    }

    @FXML
    public void onRepeatPress() throws Exception {
    	queue.setRepeatState(repeatButton.isSelected());
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
