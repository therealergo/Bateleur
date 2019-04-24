package com.bateleur.test.model;

import com.bateleur.app.App;
import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.datatype.BAudioLocal;
import com.bateleur.app.datatype.BReference;
import com.bateleur.app.model.LibraryModel;
import com.bateleur.app.model.QueueModel;
import com.bateleur.app.model.SettingsModel;
import com.therealergo.main.Main;
import com.therealergo.main.MainException;
import de.saxsys.javafx.test.JfxRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import static junit.framework.TestCase.*;

@RunWith(JfxRunner.class)
public class QueueModelTest {
    private QueueModel queueModel;

    private static SettingsModel settings;
    private static LibraryModel library;
    private static BAudio testAudio;


    @BeforeClass
    public static void setupClass() throws Exception {
        try {
            Main.mainStop();
        }
        catch (MainException e) { }
        Main.mainInit(App.class, new String[]{});

    	// Ensure that there are no existing library files
    	Main.resource.getResourceFolderLocal("test_out>QueueModelTest>library").create().delete();

        settings = new SettingsModel(Main.resource.getResourceFileLocal("settings.ser"));
        library = new LibraryModel(settings, Main.resource.getResourceFolderLocal("test_out>QueueModelTest>library"));
        library.update();
    }

    /**
     * Refreshes the state of the playbackModel being tested
     */
    @Before
    public void setup() throws Exception {
        queueModel = new QueueModel(settings);

        // Ensure that there is no existing metadata file
        Main.resource.getResourceFileLocal("test_out>QueueModelTest>test_meta.ser").create().delete();

        testAudio = new BAudioLocal(settings,
        		                    Main.resource.getResourceFileClass("test_in>test.mp3", App.class),
                                    new BReference(settings));
        queueModel.setQueue(library, testAudio);
    }



    @Test
    public void test_lastAudioQueueEn_skipForwards_firstSong() {
        // Given
        queueModel.setQueueState(true);
        BAudio startingAudio = library.getByReference(queueModel.get());

        // When
        queueModel.skipForwards();
        BAudio nextAudio = library.getByReference(queueModel.get());

        // Then
        assertNotNull(nextAudio);
        assertNotSame(startingAudio, nextAudio);
        assertNotSame(nextAudio, testAudio);
    }

    @Test
    public void test_queueLoaded_setQueue_changesQueue() {

    }

    /**
     * Validates that the queue status changes within the settings
     */
    @Test
    public void test_firstAudioNoQueue_setQueueEnabled_queueEnabled() {

    }

    /**
     * Validates that the Queue can load the previous song
     */
    @Test
    public void test_firstAudioQueueEn_skipBackwards_lastSong() {
        // Given
        queueModel.setQueueState(true);
        BAudio startingAudio = library.getByReference(queueModel.get());
        
        // When
        queueModel.skipBackwards();
        BAudio prevAudio = library.getByReference(queueModel.get());
        
        // Then
        assertNotNull(prevAudio);
        assertNotSame(startingAudio, prevAudio);
        assertNotSame(prevAudio, testAudio);
    }

    /**
     * Validates that the Queue can load the next song
     */
    @Test
    public void test_firstAudioNoQueue_skipForwards_loadsNextSong() {
        BAudio startingAudio = library.getByReference(queueModel.get());

        queueModel.skipForwards();
        BAudio nextAudio = library.getByReference(queueModel.get());

        // Then
        assertNotNull(nextAudio);
        assertNotSame(startingAudio, nextAudio);
        assertNotSame(nextAudio, testAudio);
    }

    /**
     * Validates that the queue can load the previous song
     */
    @Test
    public void test_lastAudioNoQueue_skipBackwards_loadsPrevSong() {

    }

    /**
     * Validates that advancing the Queue past the last song
     * finishes the Queue
     */
    @Test
    public void test_lastAudioNoQueue_skipForwards_endsQueue() {

    }

    /**
     * Validates that the regressing the Queue past the first song
     * finishes the Queue
     */
    @Test
    public void test_firstAudioNoQueue_skipBackwards_endsQueue() {

    }


    /**
     * Validates that shuffle creates a new Queue
     */
    @Test
    public void test_shuffleOff_shuffleQueue_createsNewQueue() {

    }

    /**
     * Validates that disabling shuffle recreates the original Queue
     */
    @Test
    public void test_ShuffleOn_ShuffleQueue_recreatesOldQueue() {

    }
}
