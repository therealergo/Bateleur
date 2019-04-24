package com.bateleur.app.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import com.bateleur.app.App;
import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.view.BBackgroundCanvas;
import com.bateleur.app.view.BSliderCanvas;
import com.melloware.jintellitype.IntellitypeListener;
import com.melloware.jintellitype.JIntellitype;
import com.therealergo.main.Main;
import com.therealergo.main.MainException;
import com.therealergo.main.os.EnumOS;
import com.therealergo.main.resource.ResourceFile;

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

public class PlaybackController implements IntellitypeListener {
	/** FXML-injected component references. */
	@FXML private BBackgroundCanvas backgroundCanvas;
	@FXML private AnchorPane playbackBarBG;
	@FXML private AnchorPane playbackBar;
	@FXML private AnchorPane playbackBarLeft;
	@FXML private AnchorPane playbackBarLeftFG;
	@FXML private AnchorPane playbackBarRightFG;
	@FXML private AnchorPane playbackBarBO;
	@FXML private Label textBot;
	@FXML private Label textMid;
	@FXML private Label textTop;
	@FXML private ImageView playbackImage;
	@FXML private ColumnConstraints playbackImageContainer;
	@FXML private Button skipBackwardButton;
	@FXML private Button skipForwardButton;
	@FXML private ToggleButton playPauseButton;
	@FXML private ToggleButton shuffleButton;
	@FXML private ToggleButton queueButton;
	@FXML private ToggleButton repeatButton;
	@FXML private ImageView skipBackwardButtonImage;
	@FXML private ImageView skipForwardButtonImage;
	@FXML private ImageView playPauseButtonImage_O;
	@FXML private ImageView shuffleButtonImage_O;
	@FXML private ImageView queueButtonImage_O;
	@FXML private ImageView repeatButtonImage_O;
	@FXML private ImageView playPauseButtonImage_I;
	@FXML private ImageView shuffleButtonImage_I;
	@FXML private ImageView queueButtonImage_I;
	@FXML private ImageView repeatButtonImage_I;
	@FXML private Slider seekBar;
	@FXML private BSliderCanvas seekBarCanvas;
	@FXML private Slider volumeBar;
	@FXML private BSliderCanvas volumeBarCanvas;
	
	/** Reference to this PlaybackController's MasterController. */
	public MasterController master;
	
	/** TEMPORARY flag used to resolve seek bar dragging */
	private boolean TEMP_OSS = false;

	/**
	 * Perform any initialization required by this PlaybackController.
	 * Should only be called by the MasterController when the master is ready and this PlaybackController is to be initialized.
	 * @param master This PlaybackController's master controller.
	 */
	public void initialize(MasterController master) {
		this.master = master;
		
		// Colorize the playback controls to the FG color
		playbackBarLeftFG.setEffect(master.playbackColorAnimation.lightingFG);
		playbackBarRightFG.setEffect(master.playbackColorAnimation.lightingFG);

		// Colorize the background fade to the BG color
		playbackBarBG.setEffect(master.playbackColorAnimation.lightingBG);
		
		// Setup the play/pause button
		master.playback.onPlayEvent.addListener(() -> {
			playPauseButtonImage_O.setOpacity(0.0);
			playPauseButtonImage_I.setOpacity(1.0);
		});
		master.playback.onPauseEvent.addListener(() -> {
			playPauseButtonImage_O.setOpacity(1.0);
			playPauseButtonImage_I.setOpacity(0.0);
		});
		
		// Setup the shuffle enable/disable button
		shuffleButton.setSelected(master.queue.isShuffleEnabled());
		shuffleButtonImage_O.setOpacity(shuffleButton.isSelected() ? 1.0 : 0.0);
		shuffleButtonImage_I.setOpacity(shuffleButton.isSelected() ? 0.0 : 1.0);
		
		// Setup the queue enable/disable button
		queueButton  .setSelected(master.queue.isQueueEnabled()  );
		queueButtonImage_O  .setOpacity(queueButton  .isSelected() ? 1.0 : 0.0);
		queueButtonImage_I  .setOpacity(queueButton  .isSelected() ? 0.0 : 1.0);

		// Setup the repeat enable/disable button
		repeatButton .setSelected(master.queue.isRepeatEnabled() );
		repeatButtonImage_O .setOpacity(repeatButton .isSelected() ? 1.0 : 0.0);
		repeatButtonImage_I .setOpacity(repeatButton .isSelected() ? 0.0 : 1.0);
		
		// Setup the volume bar
		volumeBar.setMin(0.0);
		volumeBar.setMax(100.0);
		volumeBar.setValue(master.playback.getVolume()*100.0);
		volumeBar.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
			onVolumeSet(new_val.doubleValue()/100.0);
		});
		
		// Setup the seek bar
		seekBar.setMin(0.0);
		seekBar.setMax(1.0);
		seekBar.setValue(0.0);
		seekBar.valueProperty().addListener((ObservableValue<? extends Number> ov, Number old_val, Number new_val) -> {
			if (TEMP_OSS == false) {
				onSeekSet(new_val.doubleValue());
			}
		});
		
		// Setup a callback to change the displayed audio info. text and image when the playing audio file changes
		master.playback.onSongChangeEvent.addListener(() -> {
			String text;
			BAudio newLoadedAudio = master.library.getByReference(master.playback.getLoadedAudio());
			
			text = newLoadedAudio.get(master.settings.AUDIO_META_ARTIST);
			textTop.setText(text.equals(master.settings.AUDIO_META_ARTIST.val) ? "" : text);
			
			text = newLoadedAudio.get(master.settings.AUDIO_META_ALBUM);
			textMid.setText(text.equals(master.settings.AUDIO_META_ALBUM.val) ? "" : text);
			
			text = newLoadedAudio.get(master.settings.AUDIO_META_TITLE);
			textBot.setText(text.equals(master.settings.AUDIO_META_TITLE.val) ? "" : text);
			
			Image im = newLoadedAudio.get(master.settings.AUDIO_META_ARTLOAD).getImageThumbnail(master.settings, newLoadedAudio);
			playbackImageContainer.setMinWidth(im.getWidth() / im.getHeight() * 107.0);
			playbackImage.setImage(im);
		});
		
		// TEMPORARY code that spawns a thread updating the seekbar's on-screen position to the current position
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
							seekBar.setValue(master.playback.getPlaybackTimeMS() / master.playback.getPlaybackLengthMS());
							TEMP_OSS = false;
							if (master.playback.getPlaybackTimeMS() >= master.playback.getPlaybackLengthMS()) {
								boolean shouldAutoPlay = master.queue.skipForwards();
								master.playback.loadAudio(
									master.library.getByReference(master.queue.get()), 
									master.settings.get(master.settings.FADE_TIME_USER)
								);
								if (shouldAutoPlay) {
									master.playback.play(master.settings.get(master.settings.FADE_TIME_USER));
								}
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
		
		// Initialize JIntellitype and its DLL only on Windows, which is the only OS that it supports
		if (Main.os.getOS().equals(EnumOS.WINDOWS)) {
			// Get the 64-bit / 32-bit specific path to the native library
			String libraryName = System.getProperty("os.arch").contains("64") ? "JIntellitype64.dll" : "JIntellitype.dll";
			
			// Get the local resource that contains the native library itself
			ResourceFile libraryFile = Main.resource.getResourceFileClass("natives>" + libraryName, App.class);
			
			// Copy the local resource native library to a temporary folder not contained within the jar
			// This has to be done because Windows cannot load a native library directly out of a jar
			File tempFile;
			try {
				tempFile = File.createTempFile("Bateleur_JIntellitype", ".dll");
				tempFile.deleteOnExit();
				Files.copy(libraryFile.toPath(), tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
			} catch (IOException e) {
				throw new MainException(PlaybackController.class, "Unable to create or write to temporary JNI library file!", e);
			}
			
			// Point JItellitype to the newly-copied native library
			JIntellitype.setLibraryLocation(tempFile.toPath().toAbsolutePath().toString());
			
			// initialize JIntellitype with the frame so all windows commands can be attached to this window
			JIntellitype.getInstance().addIntellitypeListener(this);
			
			Main.log.log("JIntellitype initialized");
		} else {
			Main.log.log(new MainException(PlaybackController.class, "JIntellitype not initialized as we are not on Windows!"));
		}
	}
	
	/**
	 * Callback triggered whenever the volume is manually changed.
	 * @param volume The volume to adjust the current playback volume to.
	 */
	public void onVolumeSet(double volume) {
		master.playback.setVolume(volume);
	}
	
	/**
	 * Callback triggered whenever seeking is performed within the current song.
	 * @param seek The time to which to seek, as a double between 0.0 and 1.0.
	 */
	public void onSeekSet(double seek) {
		master.playback.setPlaybackTimeMS(master.playback.getPlaybackLengthMS() * seek);
		master.playback.play(master.settings.get(master.settings.FADE_TIME_USER));
	}
	
	/**
	 * FXML-injected callback triggered whenever the shuffle toggle button is pressed.
	 */
	@FXML public void onShufflePress() {
		master.queue.setShuffleState(shuffleButton.isSelected());
		shuffleButtonImage_O.setOpacity(shuffleButton.isSelected() ? 1.0 : 0.0);
		shuffleButtonImage_I.setOpacity(shuffleButton.isSelected() ? 0.0 : 1.0);
	}

	/**
	 * FXML-injected callback triggered whenever the queue toggle button is pressed.
	 */
	@FXML public void onQueuePress() {
		master.queue.setQueueState(queueButton.isSelected());
		queueButtonImage_O.setOpacity(queueButton.isSelected() ? 1.0 : 0.0);
		queueButtonImage_I.setOpacity(queueButton.isSelected() ? 0.0 : 1.0);
	}

	/**
	 * FXML-injected callback triggered whenever the repeat toggle button is pressed.
	 */
	@FXML public void onRepeatPress() throws Exception {
		master.queue.setRepeatState(repeatButton.isSelected());
		repeatButtonImage_O.setOpacity(repeatButton.isSelected() ? 1.0 : 0.0);
		repeatButtonImage_I.setOpacity(repeatButton.isSelected() ? 0.0 : 1.0);
	}

	/**
	 * FXML-injected callback triggered whenever the sliding playback bar is pressed.
	 */
	@FXML public void onBarPress() throws Exception {
		master.verticalSlideAnimation.play();
	}

	/**
	 * FXML-injected callback triggered whenever the play/pause button is pressed.
	 */
	@FXML public void onPlayPausePress() {
		if (master.playback.isPlaying()) {
			master.playback.pause(master.settings.get(master.settings.FADE_TIME_USER));
		} else {
			master.playback.play(master.settings.get(master.settings.FADE_TIME_USER));
		}
	}

	/**
	 * FXML-injected callback triggered whenever the skip forwards button is pressed.
	 */
	@FXML public void onSkipForwardPress() {
		master.queue.skipForwards();
		master.playback.loadAudio(
			master.library.getByReference(master.queue.get()), 
			master.settings.get(master.settings.FADE_TIME_USER)
		);
		master.playback.play(master.settings.get(master.settings.FADE_TIME_USER));
	}

	/**
	 * FXML-injected callback triggered whenever the skip backwards button is pressed.
	 */
	@FXML public void onSkipBackwardPress() {
		master.queue.skipBackwards();
		master.playback.loadAudio(
			master.library.getByReference(master.queue.get()), 
			master.settings.get(master.settings.FADE_TIME_USER)
		);
		master.playback.play(master.settings.get(master.settings.FADE_TIME_USER));
	}
	
	/**
	 * Performs the action associated with a media key
	 * @param keyCommand the integer for JIntellitype's internal representation
	 */
	@Override
	public void onIntellitype(int keyCommand) {
		switch (keyCommand) {
			case JIntellitype.APPCOMMAND_MEDIA_PLAY_PAUSE:
				Platform.runLater(() -> {
					onPlayPausePress();
				});
				break;
				
			case JIntellitype.APPCOMMAND_MEDIA_NEXTTRACK:
				Platform.runLater(() -> {
					onSkipForwardPress();
				});
				break;
				
			case JIntellitype.APPCOMMAND_MEDIA_PREVIOUSTRACK:
				Platform.runLater(() -> {
					onSkipBackwardPress();
				});
				break;
				
			default:
				break;
		}
	}
}
