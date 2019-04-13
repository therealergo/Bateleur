package com.bateleur.app.model;

import java.util.ArrayList;
import java.util.List;

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
	
	private List<Runnable> onPlayHandlers      ;
	private List<Runnable> onPauseHandlers     ;
	private List<Runnable> onSongChangeHandlers;

	public PlaybackModel(SettingsModel settings) {
		this.settings = settings;
		
		this.player = null;
		this.loadedAudio = null;
		this.volume = 1.0;
		
		onPlayHandlers       = new ArrayList<Runnable>();
		onPauseHandlers      = new ArrayList<Runnable>();
		onSongChangeHandlers = new ArrayList<Runnable>();
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
			Media media = new Media(audio.get(settings.PLAYBACK_FILE).getFullURI());
			player = new MediaPlayer(media);
			player.setOnPlaying(() -> {
				for (int i = 0; i<onPlayHandlers.size(); i++) {
					onPlayHandlers.get(i).run();
				}
			});
			player.setOnPaused(() -> {
				for (int i = 0; i<onPauseHandlers.size(); i++) {
					onPauseHandlers.get(i).run();
				}
			});
			player.setVolume(volume);
			loadedAudio = audio;
		}
		
		for (int i = 0; i<onSongChangeHandlers.size(); i++) {
			onSongChangeHandlers.get(i).run();
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
	
	public void addPlayHandler(Runnable handler) {
		onPlayHandlers.add(handler);
	}
	
	public void removePlayHandler(Runnable handler) {
		onPlayHandlers.remove(handler);
	}
	
	public void addPauseHandler(Runnable handler) {
		onPauseHandlers.add(handler);
	}
	
	public void removePauseHandler(Runnable handler) {
		onPauseHandlers.remove(handler);
	}
	
	public void addSongChangeHandler(Runnable handler) {
		onSongChangeHandlers.add(handler);
	}
	
	public void removSongChangeHandler(Runnable handler) {
		onSongChangeHandlers.remove(handler);
	}
}
