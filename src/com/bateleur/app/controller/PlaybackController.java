package com.bateleur.app.controller;

import static com.bateleur.app.controller.PlaybackController.RepeatSetting.REPEAT_OFF;
import static com.bateleur.app.controller.PlaybackController.RepeatSetting.REPEAT_QUEUE;

import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.QueueModel;
import com.bateleur.app.model.SettingsModel;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;

public class PlaybackController {

    enum RepeatSetting {
        REPEAT_OFF,
        REPEAT_QUEUE,
        REPEAT_ONE
    }

    @FXML
    private ToggleButton playPauseButton;

    @FXML
    private Button skipBackwardButton;

    @FXML
    private Button skipForwardButton;

    @FXML
    private ToggleButton shuffleButton;

    @FXML
    private Button queueButton;

    @FXML
    private Button repeatButton;

    @FXML
    private Slider seekBar;

    @FXML
    private Slider volumeBar;

    // Regular fields

    private SettingsModel settings;

    private PlaybackModel playback;

    private QueueModel queue;

    private RepeatSetting repeatSetting = REPEAT_OFF;    // Default is no repeating of a queue

    public PlaybackController(SettingsModel settings, PlaybackModel playback, QueueModel queue) {
        this.settings = settings;
        this.playback = playback;
        this.queue = queue;
    }
    
    @FXML
    public void initialize() {
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
    		onSeekSet(new_val.doubleValue());
        });
    }

    @FXML
    public void onRepeatPress() throws Exception {
        // TODO: Change the appearance of the button according with change
        switch (repeatSetting) {

            case REPEAT_OFF:
                repeatSetting = REPEAT_QUEUE;
                queue.setRepeat(false);
                break;

            case REPEAT_QUEUE:
                repeatSetting = REPEAT_OFF;
                queue.setRepeat(true);
                break;

            case REPEAT_ONE:
                repeatSetting = REPEAT_OFF;
                queue.setRepeat(true);   // TODO: think of how to implement repeat_one
                break;

            default:
                throw new Exception("what')");
        }
    }

    @FXML
    public void onShufflePress() {
        if (queue.isShuffleEnabled()) {
            queue = queue.shuffle();
        }
        else {
            queue.unshuffle();
        }

    }

    @FXML
    public void onQueuePress() {
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
        playback.loadAudio(queue.skipForwards());
        playback.play(settings.get(settings.FADE_TIME_USER));
    }

    @FXML
    public void onSkipBackwardPress() {
        playback.loadAudio(queue.skipBackwards());
        playback.play(settings.get(settings.FADE_TIME_USER));
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
