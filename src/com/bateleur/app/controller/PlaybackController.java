package com.bateleur.app.controller;

import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.QueueModel;
import com.bateleur.app.model.SettingsModel;

import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;

public class PlaybackController {

    enum RepeatSetting {
        REPEAT_OFF,
        REPEAT_QUEUE,
        REPEAT_ONE
    }

    @FXML
    private Label textBot;

    @FXML
    private Label textTop;

    @FXML
    private ToggleButton playPauseButton;

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

    public PlaybackController(SettingsModel settings, PlaybackModel playback, QueueModel queue) {
        this.settings = settings;
        this.playback = playback;
        this.queue = queue;
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
    }
    
    private void updateText() {
    	textTop.setText(playback.getLoadedAudio().get(settings.AUDIO_PROP_TITLE));
    	textBot.setText(playback.getLoadedAudio().get(settings.AUDIO_PROP_ARTIST));
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
