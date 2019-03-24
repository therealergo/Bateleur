package com.bateleur.app.model;

import com.bateleur.app.datatype.BAudio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

public class PlaybackModel {
	private final SettingsModel settings;
	
	private MediaPlayer player;
	private BAudio loadedAudio;
	private double volume;

	public PlaybackModel(SettingsModel settings) {
		this.settings = settings;
		
		this.player = null;
		this.loadedAudio = null;
		this.volume = 1.0;
	}

	public boolean isAudioLoaded() {
		return loadedAudio != null;
	}
	
	public BAudio getLoadedAudio() {
		return loadedAudio;
	}

	public void loadAudio(BAudio audio, int fadeOutTimeMS) {
		if (player != null) {
			player.dispose();
			loadedAudio = null;
		}
		
		if (audio == null) {
			player = null;
			loadedAudio = null;
		} else {
			try {
				Media media = new Media(audio.get(settings.PLAYBACK_URI).toString());
				player = new MediaPlayer(media);
				player.setVolume(volume);
			} catch (Exception e) { //TODO: This is a temporary fix for crashes from unsupported formats
			}
			loadedAudio = audio;
		}
	}

	public void play(int fadeTimeMS) {
		if (player == null) {
			return;
		}

		player.play();
	}

	public void pause(int fadeTimeMS) {
		if (player == null) {
			return;
		}

		player.pause();
	}
	
	public boolean isPlaying() {
		return player.getStatus().equals(Status.PLAYING);
	}

	public double getPlaybackLengthMS() {
		if (player == null) {
			return 0.0;
		}

		return player.getTotalDuration().toMillis();
	}

	public double getPlaybackTimeMS() {
		if (player == null) {
			return 0.0;
		}

		return player.getCurrentTime().toMillis();
	}

	public void setPlaybackTimeMS(double time) {
		if (player == null) {
			return;
		}

		player.seek(new Duration(time));
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
		if (player != null) {
			player.setVolume(volume);
		}
	}
}
