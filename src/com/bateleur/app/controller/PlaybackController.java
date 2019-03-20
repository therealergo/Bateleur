package com.bateleur.app.controller;

import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.QueueModel;
import com.bateleur.app.model.SettingsModel;
import javafx.event.ActionEvent;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import static com.bateleur.app.controller.PlaybackController.RepeatSetting.*;

public class PlaybackController {

    enum RepeatSetting {
        REPEAT_OFF,
        REPEAT_QUEUE,
        REPEAT_ONE
    }

    @FXML
    private Button playButton;

    @FXML
    private Button skipButton;

    @FXML
    private Button forwardButton;

    @FXML
    private Button shuffleButton;

    @FXML
    private Button queueButton;

    @FXML
    private Button repeatButton;

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
    public void onRepeatPress() throws Exception {

        // TODO: Change the appearance of the button according with change
        switch (repeatSetting) {

            case REPEAT_OFF:
                repeatSetting = REPEAT_QUEUE;
                break;

            case REPEAT_QUEUE:
                repeatSetting = REPEAT_OFF;
                break;

            case REPEAT_ONE:
                repeatSetting = REPEAT_OFF;
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

    public void onQueuePress() {

    }

    public void onPlayPausePress() {

    }

    public void onSkipForwardPress() {

    }

    public void onSkipBackwardPress() {

    }

    public void onPlayTimeIncrease() {

    }

    public void onVolumeSet(float volume) {

    }

    public void onSeekSet(float volume) {

    }
}
