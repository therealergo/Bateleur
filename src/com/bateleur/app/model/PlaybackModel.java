package com.bateleur.app.model;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.datatype.BReference;
import com.therealergo.main.Main;
import com.therealergo.main.NilEvent;
import com.therealergo.main.resource.ResourceFile;

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

	public final NilEvent onPlayEvent      ;
	public final NilEvent onPauseEvent     ;
	public final NilEvent onSongChangeEvent;

	public PlaybackModel(SettingsModel settings) {
		this.settings = settings;

		this.player = null;
		createdFileMap = new HashMap<>();

		onPlayEvent       = new NilEvent();
		onPauseEvent      = new NilEvent();
		onSongChangeEvent = new NilEvent();
	}

	public boolean isAudioLoaded() {
		return settings.get( settings.PLAY_CUR_AUDIO_REF ).equals( BReference.NO_MEDIA_REF );
	}

	public BReference getLoadedAudio() {
		return settings.get( settings.PLAY_CUR_AUDIO_REF );
	}
	
	public void loadFromSavedState(LibraryModel library) {
		boolean shouldPlay = settings.get(settings.PLAY_IS_PLAYING);
		
		loadAudio(library.getByReference(settings.get( settings.PLAY_CUR_AUDIO_REF )), 0);
		
		if (shouldPlay) {
			play(0);
		} else {
			pause(0);
		}
	}

	public void loadAudio(BAudio audio, int fadeOutTimeMS) {
		if (player != null) {
			player.dispose();
		}
		
		onPauseEvent.accept();
		settings.set( settings.PLAY_IS_PLAYING.to(false) );
		settings.set( settings.PLAY_CUR_AUDIO_REF.to(BReference.NO_MEDIA_REF) );
		
		if (audio == null) {
			player = null;
			settings.set( settings.PLAY_CUR_AUDIO_REF.to(BReference.NO_MEDIA_REF) );
		} else {
			Main.log.log("Loaded audio: " + audio.get(settings.AUDIO_REFERENCE));
			
			ResourceFile playbackFile = audio.get(settings.AUDIO_REFERENCE).getPlaybackFile();
			String       playbackURI  = playbackFile.getFullURI();
			String       fileExt      = playbackFile.getExtension();
			Media        loadedMedia  = null;
			
			switch (fileExt.toLowerCase()) {
				case "mp3":
					loadedMedia = new Media(playbackURI);
					break;
				case "flac":
					try {
						if (createdFileMap.containsKey(playbackURI)) {
							loadedMedia = new Media(createdFileMap.get(playbackURI));
						} else {
							loadedMedia = BFlacToWav.decode(settings, audio);
						}
					} catch (IOException e) {
						Main.log.logErr("Error decoding FLAC in PlaybackModel");
					} catch (URISyntaxException q) {
						Main.log.logErr("Error decoding FLAC in PlaybackModel");
					}
					break;
				default:
					break;
			}
			
			if (loadedMedia != null) {
				player = new MediaPlayer(loadedMedia);
				player.setOnPlaying(() -> {
					onPlayEvent.accept();
					settings.set( settings.PLAY_IS_PLAYING.to(true) );
				});
				player.setOnPaused(() -> {
					onPauseEvent.accept();
					settings.set( settings.PLAY_IS_PLAYING.to(false) );
				});
				player.setVolume( settings.get(settings.PLAY_CUR_VOLUME) );
				settings.set( settings.PLAY_CUR_AUDIO_REF.to(audio.get(settings.AUDIO_REFERENCE)) );
			}
		}
		
		onSongChangeEvent.accept();
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
		return settings.get(settings.PLAY_CUR_VOLUME);
	}

	public void setVolume(double volume) {
		settings.set( settings.PLAY_CUR_VOLUME.to(volume) );
		if (player != null) {
			player.setVolume(volume);
		}
	}
	
	// Decoding of the FLAC follows the sample application in io.nayuki.flac.app very closely
	// The library is licensed under the GPL.
	private static class BFlacToWav {

		private static DataOutputStream out;

		static Media decode(SettingsModel settings, BAudio audio) throws IOException, URISyntaxException {
			ResourceFile playbackFile = audio.get(settings.AUDIO_REFERENCE).getPlaybackFile();
			URI          requestedURI = playbackFile.toURI ();
			File         inFile       = playbackFile.toFile();
			//TODO: Add the hash of the original FLAC file here, so that if the original file changes we don't re-use the old converted file
			File         outFile      = File.createTempFile("Bateleur_Convert_", ".wav");
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
