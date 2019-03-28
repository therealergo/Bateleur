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

    private PlaybackModel playbackModel;

    private static SettingsModel settings;

    @BeforeClass
    public static void setupClass() throws Exception {
        Main.mainInit(App.class, new String[]{});
        Thread.sleep(DELAY_TIME_MS);    // wait for Main to init correctly

        settings = new SettingsModel(Main.resource.getResourceFileClass("settings.ser", App.class));
    }

    @Before
    public void setup() {
        playbackModel = new PlaybackModel(settings);
    }

    @Test
    public void test_noAudio_loadAudio_loadsAudio() throws Exception {
        // Given
        BAudio audio = new BAudioLocal(settings,
                                       Main.resource.getResourceFileLocal("testPlaybackModel>__meta_test"),
                                       Main.resource.getResourceFileLocal("testPlaybackModel>test.mp3").getPath().toUri());

        // When
        playbackModel.loadAudio(audio, FADE_TIME);

        // Then
        assertTrue(playbackModel.isAudioLoaded());
        assertEquals(audio, playbackModel.getLoadedAudio());
    }

    public void test_loadedAudio_loadAudio_loadsNewAudio() throws Exception {
        // Given
        BAudio originalAudio = new BAudioLocal(settings,
                                               Main.resource.getResourceFileLocal("testPlaybackModel>__meta_test"),
                                               Main.resource.getResourceFileLocal("testPlaybackModel>test.mp3").getPath().toUri());

        BAudio audio = new BAudioLocal(settings,
                                       Main.resource.getResourceFileLocal("testPlaybackModel>__meta_test"),
                                       Main.resource.getResourceFileLocal("testPlaybackModel>0.mp3").getPath().toUri());
        assert !(audio.equals(originalAudio));    // validates the test environment was created correctly, not unit

        playbackModel.loadAudio(originalAudio, FADE_TIME);
        playbackModel.play(FADE_TIME);

        // When
        playbackModel.loadAudio(audio, FADE_TIME);
        // Then
        assertTrue(playbackModel.isAudioLoaded());
        assertEquals(audio, playbackModel.getLoadedAudio());
    }


}
