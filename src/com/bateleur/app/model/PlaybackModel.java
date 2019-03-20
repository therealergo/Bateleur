package com.bateleur.app.model;

import java.net.URI;

import com.bateleur.app.datatype.BAudio;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class PlaybackModel {
	private MediaPlayer player;
	private BAudio loadedAudio;
	private double volume;

	public PlaybackModel() {
		player = null;
		volume = 1.0;
	}

	public boolean isAudioLoaded() {
		return loadedAudio != null;
	}
	
	public BAudio getLoadedAudio() {
		return loadedAudio;
	}

	public void loadAudio(BAudio audio) {
		if (audio == null) {
			player = null;
			loadedAudio = null;
		} else {
			Media media = new Media(audio.<URI>getMetadata("__playbackSourceURI").toString());
			player = new MediaPlayer(media);
			player.setVolume(volume);
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
