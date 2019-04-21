package com.bateleur.test.model;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNotSame;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;
import static org.mockito.ArgumentMatchers.any;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.bateleur.app.App;
import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.datatype.BAudioLocal;
import com.bateleur.app.datatype.BReference;
import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.SettingsModel;
import com.therealergo.main.Main;
import com.therealergo.main.MainException;

import de.saxsys.javafx.test.JfxRunner;

@RunWith(JfxRunner.class)
public class PlaybackModelTest {
    private static final int FADE_TIME = 0;
    private static final long PLAY_TIME = 175L;

    private PlaybackModel playbackModel;

    private static SettingsModel settings;
    private static BAudio testAudio;

    @BeforeClass
    public static void setupClass() throws Exception {
        try {
            Main.mainStop();
        }
        catch (MainException e) { }
        Main.mainInit(App.class, new String[]{});

    	// Ensure that there is no existing metadata file
    	Main.resource.getResourceFileLocal("test_out>PlaybackModelTest>test_meta.ser").create().delete();

        settings = new SettingsModel(Main.resource.getResourceFileLocal("settings.ser"));
        testAudio = new BAudioLocal(settings,
                                   Main.resource.getResourceFileLocal("test_out>PlaybackModelTest>test_meta.ser"),
                                   new BReference(Main.resource.getResourceFileClass("test_in>test.mp3", App.class)));
    }

    /**
     * Refreshes the state of the playbackModel being tested
     */
    @Before
    public void setup() {
        playbackModel = new PlaybackModel(settings);
    }

    /**
     * Tests loading audio to check that MediaPlayer's loaded audio is the same as the test audio
     */
    @Test
    public void test_noAudio_loadAudio_loadsAudio() {
        // Given

        // When
        playbackModel.loadAudio(testAudio, FADE_TIME);

        // Then
        assertTrue(playbackModel.isAudioLoaded());
        assertEquals(testAudio, playbackModel.getLoadedAudio());
    }

    /**
     * Tests loading audio correctly changes the loaded audio in the MediaPlayer of PlaybackMddel
     * @throws Exception error in creating BAudio file for test
     */
    @Test
    public void test_loadedAudio_loadAudio_loadsNewAudio() throws Exception {
        // Given
        BAudio originalAudio = new BAudioLocal(settings,
                                               Main.resource.getResourceFileLocal("test_out>PlaybackModelTest>test_meta.ser"),
                                               new BReference(Main.resource.getResourceFileClass("test_in>test.mp3", App.class)));

        assert !(testAudio.equals(originalAudio));    // validates the test environment was created correctly, not unit

        playbackModel.loadAudio(originalAudio, FADE_TIME);
        playbackModel.play(FADE_TIME);

        // When
        playbackModel.loadAudio(testAudio, FADE_TIME);

        // Then
        assertTrue(playbackModel.isAudioLoaded());
        assertEquals(testAudio, playbackModel.getLoadedAudio());
    }

    /**
     * Pauses the thread to verify that the playback model is playing. This is necessary to avoid
     * MediaPlayer.Status.UNKNOWN when validating player state.
     * @param playing whether the desired state is playing or not
     * @throws InterruptedException part of thread.sleep
     */
    private boolean assertPlaying(boolean playing) throws InterruptedException {
    	// Allow up to 50 * 100 = 5000 milliseconds = 5 seconds for playback to begin
        for (int i = 0; i < 50; i++) {
            if (playing) {
                if (playbackModel.isPlaying()) {
                    assertTrue(playbackModel.isPlaying());
                    return true;    // cease test
                }
            } else {
                if (!playbackModel.isPlaying()) {
                    assertFalse(playbackModel.isPlaying());
                    return true;    // cease test
                }
            }
            Thread.sleep(100);
        }
        return false;
    }

    /**
     * This tests that the PlaybackModel begins playback
     * @throws InterruptedException if the Thread is interrupted
     */
    @Test
    public void test_audioLoaded_play_isPlaying()
    throws InterruptedException {
        // Given
        playbackModel.loadAudio(testAudio, FADE_TIME);

        // When
        playbackModel.play(FADE_TIME);

        // Then
        assertTrue(playbackModel.isAudioLoaded());
        if (!assertPlaying(true)) {
            fail();
        }
    }

    /**
     * This tests that the PlaybackModel pauses audio playback
     */
    @Test
    public void test_audioLoadedNotPlaying_isPlaying_false() {
        // Given
        playbackModel.loadAudio(testAudio, FADE_TIME);
        playbackModel.play(FADE_TIME);

        boolean playingStatus = playbackModel.isPlaying();

        // Then
        assertTrue(playbackModel.isAudioLoaded());
        assertFalse(playingStatus);
    }

    /**
     * Validates that the pause method pauses playback
     * @throws InterruptedException if the Thread is interrupted
     */
    @Test
    public void test_audioLoadedPlaying_pause_notPlaying()
    throws InterruptedException {
        // Given
        playbackModel.loadAudio(testAudio, FADE_TIME);
        playbackModel.play(FADE_TIME);
        if (!assertPlaying(true)) {
            fail();
        }

        // When
        playbackModel.pause(FADE_TIME);
        assertPlaying(false);

        // Then
        assertTrue(playbackModel.isAudioLoaded());
        assertFalse(playbackModel.isPlaying());
    }

    /**
     * Validates that
     * @throws InterruptedException if the Thread is interrupted
     */
    @Test
    public void test_audioPlayingPause_play_resumesFromTime()
    throws InterruptedException {
        // Given
        playbackModel.loadAudio(testAudio, FADE_TIME);
        playbackModel.play(FADE_TIME);
        if (!assertPlaying(true)) {
            fail();
        }
        Thread.sleep(PLAY_TIME);    // allows the media player to "play" the music for some time
        playbackModel.pause(FADE_TIME);
        double pausedTime = playbackModel.getPlaybackTimeMS();

        // When
        playbackModel.play(FADE_TIME);
        assertPlaying(true);
        Thread.sleep(PLAY_TIME);

        // Then
        // TODO find reliable way to check if the player resumes from right time
        double playbackTime = playbackModel.getPlaybackTimeMS();
        assertNotSame(pausedTime, playbackTime);
        assertTrue(pausedTime < playbackTime);
    }

    /**
     * Verifies that the playback volume changes correctly in the player
     */
    @Test
    public void test_originalVolume_setVolume_changes() {
        // Given
        playbackModel.loadAudio(testAudio, FADE_TIME);
        double originalVolume = playbackModel.getVolume();

        // When
        playbackModel.setVolume(any(Double.class));    // any value since we have no value checking in the method

        // Then
        assertFalse(originalVolume == playbackModel.getVolume());
    }

    /**
     * Verifies that the playback time changes correctly in the player
     */
    @Test
    public void test_originalPlaybackTime_setPlaybackTime_changes()
    throws InterruptedException {
        // Given
        playbackModel.loadAudio(testAudio, FADE_TIME);
        playbackModel.play(FADE_TIME);
        if (!assertPlaying(true)) {
            fail();
        }
        double originalPlaybackTime = playbackModel.getPlaybackTimeMS();

        playbackModel.pause(FADE_TIME);
        if (!assertPlaying(false)) {
            fail();
        }

        // When
        playbackModel.setPlaybackTimeMS(playbackModel.getPlaybackLengthMS() * 0.5);

        // Then
        // TODO find reliable way to check if the player resumes from right time
        assertTrue(originalPlaybackTime <= playbackModel.getPlaybackTimeMS());
    }

}
