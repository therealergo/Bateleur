package com.bateleur.app.model;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import com.bateleur.app.datatype.BAudio;

import com.therealergo.main.Main;
import io.nayuki.flac.common.StreamInfo;
import io.nayuki.flac.decode.DataFormatException;
import io.nayuki.flac.decode.FlacDecoder;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.util.Duration;

public class PlaybackModel {
	private final SettingsModel settings;

	private MediaPlayer player;
	private static HashMap<String, String> createdFileMap;
	private BAudio loadedAudio;
	private double volume;

	private List<Runnable> onPlayHandlers      ;
	private List<Runnable> onPauseHandlers     ;
	private List<Runnable> onSongChangeHandlers;

	public PlaybackModel(SettingsModel settings) {
		this.settings = settings;

		this.player = null;
		createdFileMap = new HashMap<>();
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
			for (int i = 0; i<onPauseHandlers.size(); i++) {
				onPauseHandlers.get(i).run();
			}
			player.dispose();
			loadedAudio = null;
		}

		if (audio == null) {
			player = null;
			loadedAudio = null;
		} else {
//			String playbackURI = audio.get(settings.PLAYBACK_URI).toString();
			String playbackURI = audio.get(settings.PLAYBACK_FILE).getFullURI().toString();
			Optional<String> fileExt = getFileExtension(playbackURI);

			Optional<Media> optionalMedia = Optional.empty();
			if (fileExt.isPresent()) {

				switch (fileExt.get().toLowerCase()) {
					case "mp3":
						optionalMedia = Optional.of(new Media(playbackURI));
						break;
					case "flac":
						try {

							if (createdFileMap.containsKey(playbackURI)) {
								optionalMedia = Optional.of(new Media(createdFileMap.get(playbackURI)));
							}
							else {
								optionalMedia = Optional.of(BFlacToWav.decode(settings, audio));
							}
						}
						catch (IOException e) {
							Main.log.logErr("Error decoding FLAC in PlaybackModel");
						}
						catch (URISyntaxException q) {
							Main.log.logErr("Error decoding FLAC in PlaybackModel");
						}
						break;
					default:
						break;
				}
			}

			if (optionalMedia.isPresent()) {
				Media media = optionalMedia.get();
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

	// invoke this on exit of the program
	// this will delete the created WAVs from the user library
	public boolean deleteFilesInMap() {
		for (String q : createdFileMap.keySet()) {
			File wav = null;
			try {
				wav = new File(new URI(createdFileMap.get(q)));
			} catch (URISyntaxException e) {
				continue;
			}
			if (!wav.delete()) {
				return false;
			}
		}
		return true;
	}

	private Optional<String> getFileExtension(String filename) {
		return Optional.ofNullable(filename)
				.filter(f -> f.contains("."))
				.map(f -> f.substring(filename.lastIndexOf(".") + 1));
	}


	// Decoding of the FLAC follows the sample application in io.nayuki.flac.app very closely
	// The library is licensed under the GPL.
	private static class BFlacToWav {

		private static DataOutputStream out;

		static Media decode(SettingsModel settings, BAudio audio) throws IOException, URISyntaxException {
//			String playbackURI = audio.get(settings.PLAYBACK_FILE).getFullURI().toString();
			URI requestedURI = new URI(audio.get(settings.PLAYBACK_FILE).getFullURI());
            Path requestedPath = Paths.get(requestedURI);
			File inFile = new File(requestedURI);

//			String tempURIString = audio.get(settings.AUDIO_PROP_TITLE) + ".wav";

			StringBuilder outFileName = new StringBuilder();
			outFileName.append(requestedPath.getParent().toString());
			outFileName.append("\\");
			outFileName.append(requestedURI.hashCode());
			outFileName.append(".wav");
//            String outputPathName = requestedPath.getParent().toString() + '\\' + tempURIString;
			File outFile = new File(outFileName.toString());
			outFile.deleteOnExit();

			createdFileMap.putIfAbsent(requestedURI.toString(), outFile.toURI().toString());

			// Decode input FLAC file
			StreamInfo streamInfo;
			int[][] samples;
			try (FlacDecoder dec = new FlacDecoder(inFile)) {

				// Handle metadata header blocks
				while (dec.readAndHandleMetadataBlock() != null) ;
				streamInfo = dec.streamInfo;
				if (streamInfo.sampleDepth % 8 != 0)
					throw new UnsupportedOperationException("Only whole-byte sample depth supported");

				// Decode every block
				samples = new int[streamInfo.numChannels][(int) streamInfo.numSamples];
				for (int off = 0; ; ) {
					int len = dec.readAudioBlock(samples, off);
					if (len == 0)
						break;
					off += len;
				}
			}

			// Check audio MD5 hash
			byte[] expectHash = streamInfo.md5Hash;
			if (Arrays.equals(expectHash, new byte[16]))
				System.err.println("Warning: MD5 hash field was blank");
			else if (!Arrays.equals(StreamInfo.getMd5Hash(samples, streamInfo.sampleDepth), expectHash))
				throw new DataFormatException("MD5 hash check failed");
			// Else the hash check passed

			// Start writing WAV output file
			int bytesPerSample = streamInfo.sampleDepth / 8;
			try (DataOutputStream out = new DataOutputStream(
					new BufferedOutputStream(new FileOutputStream(outFile)))) {
				BFlacToWav.out = out;

				// Header chunk
				int sampleDataLen = samples[0].length * streamInfo.numChannels * bytesPerSample;
				out.writeInt(0x52494646);  // "RIFF"
				writeLittleInt32(sampleDataLen + 36);
				out.writeInt(0x57415645);  // "WAVE"

				// Metadata chunk
				out.writeInt(0x666D7420);  // "fmt "
				writeLittleInt32(16);
				writeLittleInt16(0x0001);
				writeLittleInt16(streamInfo.numChannels);
				writeLittleInt32(streamInfo.sampleRate);
				writeLittleInt32(streamInfo.sampleRate * streamInfo.numChannels * bytesPerSample);
				writeLittleInt16(streamInfo.numChannels * bytesPerSample);
				writeLittleInt16(streamInfo.sampleDepth);

				// Audio data chunk ("data")
				out.writeInt(0x64617461);  // "data"
				writeLittleInt32(sampleDataLen);
				for (int i = 0; i < samples[0].length; i++) {
					for (int j = 0; j < samples.length; j++) {
						int val = samples[j][i];
						if (bytesPerSample == 1)
							out.write(val + 128);  // Convert to unsigned, as per WAV PCM conventions
						else {  // 2 <= bytesPerSample <= 4
							for (int k = 0; k < bytesPerSample; k++)
								out.write(val >>> (k * 8));  // Little endian
						}
					}
				}
				return new Media(outFile.toURI().toString());
			}
		}

		private static void writeLittleInt16(int x) throws IOException {
			out.writeShort(Integer.reverseBytes(x) >>> 16);
		}

		private static void writeLittleInt32(int x) throws IOException {
			out.writeInt(Integer.reverseBytes(x));
		}

	}

}
