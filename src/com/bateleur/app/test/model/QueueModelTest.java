package com.bateleur.app.test.model;

import com.bateleur.app.App;
import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.datatype.BAudioLocal;
import com.bateleur.app.model.LibraryModel;
import com.bateleur.app.model.QueueModel;
import com.bateleur.app.model.SettingsModel;
import com.therealergo.main.Main;
import de.saxsys.javafx.test.JfxRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.*;

@RunWith(JfxRunner.class)
public class QueueModelTest {

    private static final long DELAY_TIME_MS = 3000L;

    private QueueModel queueModel;

    private static SettingsModel settings;
    private static LibraryModel library;
    private static BAudio testAudio;


    @BeforeClass
    public static void setupClass() throws Exception {
        Main.mainInit(App.class, new String[]{});
        Thread.sleep(DELAY_TIME_MS);    // wait for Main to init correctly
        settings = new SettingsModel(Main.resource.getResourceFileClass("settings.ser", App.class));

        for (int i = 0; i<8; i++) {
            BAudio audio = new BAudioLocal(settings, Main.resource.getResourceFileLocal("testLibraryModel>meta_test_" + i));
            audio.set(settings.TEST_VAL.to(1230+i));
        }
        library = new LibraryModel(settings, Main.resource.getResourceFolderLocal("testLibraryModel"));
    }

    /**
     * Refreshes the state of the playbackModel being tested
     */
    @Before
    public void setup() throws Exception {
        queueModel = new QueueModel(settings);
        testAudio = new BAudioLocal(settings,
                                    Main.resource.getResourceFileLocal("testPlaybackModel>__meta_test"),
                                    Main.resource.getResourceFileLocal("testPlaybackModel>test.mp3").getPath().toUri());


        queueModel.setQueue(library, library.iterator().next());
    }

    @Test
    public void test_firstAudioQueueEn_skipForwards_nextSong() {
        // Given
        queueModel.setQueueState(true);
        BAudio startingAudio = queueModel.get();

        // When
        queueModel.skipForwards();
        BAudio nextAudio = queueModel.get();

        // Then
        assertNotNull(nextAudio);
        assertNotSame(startingAudio, nextAudio);
        assertNotSame(nextAudio, testAudio);
    }

    @Test
    public void test_firstAudioQueueEn_skipBackwards_lastSong() {
        // Given
        queueModel.setQueueState(true);
        BAudio startingAudio = queueModel.get();

        // When
        queueModel.skipBackwards();
        BAudio prevAudio = queueModel.get();


        // Then
        assertNotNull(prevAudio);
        assertNotSame(startingAudio, prevAudio);
        assertNotSame(prevAudio, testAudio);
    }


}
