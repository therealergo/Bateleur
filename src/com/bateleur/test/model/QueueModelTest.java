package com.bateleur.test.model;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNotSame;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.bateleur.app.App;
import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.model.LibraryModel;
import com.bateleur.app.model.QueueModel;
import com.bateleur.app.model.SettingsModel;
import com.therealergo.main.Main;

import de.saxsys.javafx.test.JfxRunner;

@RunWith(JfxRunner.class)
public class QueueModelTest {
    private QueueModel queueModel;

    private static SettingsModel settings;
    private static LibraryModel library;
    
    private static BAudio firstAudio;

    @BeforeClass
    public static void setupClass() throws Exception {
    	// Start Main
    	if (Main.mainIsRunning()) {
            Main.mainStop();
    	}
        Main.mainInit(App.class, new String[]{});
        
        // Setup settings pointing to a test_out library folder
        settings = new SettingsModel(Main.resource.getResourceFileLocal("settings.ser"));
        settings.set(settings.LIBRARY_STORE_FOLD.to( Main.resource.getResourceFolderLocal("test_out>QueueModelTest>library") ));
        
    	// Setup library, ensuring that there are no existing library files and updating to get a freshly-filled library
    	settings.get(settings.LIBRARY_STORE_FOLD).create().delete();
        library = new LibraryModel(settings);
        library.update();
        while (library.isUpdating()) {}
    }

    /**
     * Refreshes the state of the playbackModel being tested
     */
    @Before
    public void setup() throws Exception {
    	// Create new queue
        queueModel = new QueueModel(settings);
        
        // Setup queue to point to entire library, starting at 'firstAudio'
        firstAudio = library.iterator().next();
        queueModel.setQueue(library, firstAudio);
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
        assertNotSame(nextAudio, firstAudio);
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
        assertNotSame(prevAudio, firstAudio);
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
        assertNotSame(nextAudio, firstAudio);
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
