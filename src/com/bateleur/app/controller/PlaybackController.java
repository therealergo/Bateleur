package com.bateleur.app.controller;

import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.QueueModel;
import com.bateleur.app.model.SettingsModel;
import com.bateleur.app.view.BBackgroundCanvas;

import javafx.animation.KeyValue;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;

public class PlaybackController {
	@FXML private BBackgroundCanvas backgroundCanvas;
	@FXML private AnchorPane root;
	@FXML private AnchorPane playbackBarBG;
	@FXML private AnchorPane playbackBar;
	@FXML private AnchorPane playbackBarLeft;
	@FXML private AnchorPane playbackBarLeftFG;
	@FXML private AnchorPane playbackBarRightFG;
	@FXML private AnchorPane playbackBarBO;
	@FXML private Label textBot;
	@FXML private Label textTop;
	@FXML private ImageView playbackImage;
	@FXML private ColumnConstraints playbackImageContainer;
	@FXML private Button skipBackwardButton;
	@FXML private Button skipForwardButton;
	@FXML private ToggleButton playPauseButton;
	@FXML private ToggleButton shuffleButton;
	@FXML private ToggleButton queueButton;
	@FXML private ToggleButton repeatButton;
	@FXML private ImageView playPauseButtonImage_O;
	@FXML private ImageView shuffleButtonImage_O;
	@FXML private ImageView queueButtonImage_O;
	@FXML private ImageView repeatButtonImage_O;
	@FXML private ImageView playPauseButtonImage_I;
	@FXML private ImageView shuffleButtonImage_I;
	@FXML private ImageView queueButtonImage_I;
	@FXML private ImageView repeatButtonImage_I;
	@FXML private Slider seekBar;
	@FXML private Slider volumeBar;

	public MasterController master;

	private SettingsModel settings;
	private PlaybackModel playback;
	private QueueModel queue;
	
	private boolean TEMP_OSS = false;

	public PlaybackController(SettingsModel settings, PlaybackModel playback, QueueModel queue) {
		this.settings = settings;
		this.playback = playback;
		this.queue = queue;
	}

	public void setMasterController(MasterController master) {
		this.master = master;
	}
	
	public void start() {
		playbackBarBG	  .setEffect(master.lightingBG);
		playbackBarLeftFG .setEffect(master.lightingFG);
		playbackBarRightFG.setEffect(master.lightingFG);
		playbackBarBO	  .setEffect(master.lightingBO);
		
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

		playback.addSongChangeHandler(() -> {
			textTop.setText(playback.getLoadedAudio().get(settings.AUDIO_PROP_ARTIST));
			textBot.setText(playback.getLoadedAudio().get(settings.AUDIO_PROP_TITLE));
			try {
				Image im = playback.getLoadedAudio().get(settings.AUDIO_PROP_ART).getImageThumbnail();
				playbackImageContainer.setMinWidth(im.getWidth() / im.getHeight() * 107.0);
				playbackImage.setImage(im);
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
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
							}
						}
					});
				}
			}
		}.start();
		
		// Build the vertical slide animation
		{
			// Slide the top bar label left/right when doing the vertical slide animation
	    	master.verticalSlideAnimation.onRebuild(() -> {
	    		master.verticalSlideAnimation.addKeyValue(
	    			new KeyValue(
		    			master.topBarLabel.translateXProperty(), 
		    			(1.0-master.verticalSlideAnimation.rebuildIndex()) * playbackImageContainer.getMinWidth()
	    			)
	    		);
	    	});
			
	    	// Slide the small audio file art left/right when doing the vertical slide animation
	    	master.verticalSlideAnimation.onRebuild(() -> {
	    		master.verticalSlideAnimation.addKeyValue(
	    			new KeyValue(
	    				playbackBarLeft.translateXProperty(), 
		    			-master.verticalSlideAnimation.rebuildIndex() * playbackImageContainer.getMinWidth()
	    			)
	    		);
	    	});
			
	    	// Rebuild the vertical slide animation every time the width of the small audio file art changes
			playbackImageContainer.minWidthProperty().addListener((ObservableValue<? extends Number> observableValue, Number oldVal, Number newVal) -> {
				Platform.runLater(() -> {
					master.verticalSlideAnimation.rebuild();
				});
			});
		}
	}

	@FXML public void onShufflePress() {
		queue.setShuffleState(shuffleButton.isSelected());
		shuffleButtonImage_O.setOpacity(shuffleButton.isSelected() ? 1.0 : 0.0);
		shuffleButtonImage_I.setOpacity(shuffleButton.isSelected() ? 0.0 : 1.0);
	}

	@FXML public void onQueuePress() {
		queue.setQueueState(queueButton.isSelected());
		queueButtonImage_O.setOpacity(queueButton.isSelected() ? 1.0 : 0.0);
		queueButtonImage_I.setOpacity(queueButton.isSelected() ? 0.0 : 1.0);
	}

	@FXML public void onRepeatPress() throws Exception {
		queue.setRepeatState(repeatButton.isSelected());
		repeatButtonImage_O.setOpacity(repeatButton.isSelected() ? 1.0 : 0.0);
		repeatButtonImage_I.setOpacity(repeatButton.isSelected() ? 0.0 : 1.0);
	}

	@FXML public void onBarPress() throws Exception {
		master.verticalSlideAnimation.play();
	}

	@FXML public void onPlayPausePress() {
		if (playback.isPlaying()) {
			playback.pause(settings.get(settings.FADE_TIME_USER));
		} else {
			playback.play(settings.get(settings.FADE_TIME_USER));
		}
	}

	@FXML public void onSkipForwardPress() {
		queue.skipForwards();
		playback.loadAudio(queue.get(), settings.get(settings.FADE_TIME_USER));
		playback.play(settings.get(settings.FADE_TIME_USER));
	}

	@FXML public void onSkipBackwardPress() {
		queue.skipBackwards();
		playback.loadAudio(queue.get(), settings.get(settings.FADE_TIME_USER));
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
