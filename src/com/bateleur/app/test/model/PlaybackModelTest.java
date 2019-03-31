package com.bateleur.app.test.model;

import com.bateleur.app.App;
import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.datatype.BAudioLocal;
import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.SettingsModel;
import com.therealergo.main.Main;
import de.saxsys.javafx.test.JfxRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;


import static junit.framework.TestCase.*;
import static org.mockito.ArgumentMatchers.any;

@RunWith(JfxRunner.class)
public class PlaybackModelTest {

    private static final int FADE_TIME = 0;
    private static final long DELAY_TIME_MS = 3000L;
    private static final double PLAYBACK_TIME = 10000.0;

    private PlaybackModel playbackModel;

    private static SettingsModel settings;
    private static BAudio testAudio;

    @BeforeClass
    public static void setupClass() throws Exception {
        Main.mainInit(App.class, new String[]{});
        Thread.sleep(DELAY_TIME_MS);    // wait for Main to init correctly

        settings = new SettingsModel(Main.resource.getResourceFileClass("settings.ser", App.class));
        testAudio = new BAudioLocal(settings,
                                   Main.resource.getResourceFileClass("test>PlaybackModelTest>__meta_test", App.class),
                                   Main.resource.getResourceFileClass("test_in>test.mp3", App.class).getPath().toUri());
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
                                               Main.resource.getResourceFileClass("test_out>PlaybackModelTest>__meta_test", App.class),
                                               Main.resource.getResourceFileClass("test_in>test.mp3", App.class).getPath().toUri());

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
     * Pauses the thread to verify that the playback model is playing
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
     */
    @Test
    public void test_audioLoaded_play_isPlaying()
    throws Exception {
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
    public void test_audioLoadedNotPlaying_isPlaying_false()
    throws Exception {
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
    public void test_originalPlaybackTime_setPlaybackTime_changes() {
        // Given
        playbackModel.loadAudio(testAudio, FADE_TIME);
        double originalPlaybackTime = playbackModel.getPlaybackTimeMS();

        // When
        playbackModel.setPlaybackTimeMS(PLAYBACK_TIME);

        assertFalse(originalPlaybackTime == playbackModel.getPlaybackTimeMS());
    }

}
