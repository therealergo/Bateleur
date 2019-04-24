package com.bateleur.test.datatype;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.bateleur.app.App;
import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.datatype.BAudioLocal;
import com.bateleur.app.datatype.BReference;
import com.bateleur.app.model.SettingsModel;
import com.therealergo.main.Main;
import com.therealergo.main.MainException;

import de.saxsys.javafx.test.JfxRunner;

@RunWith(JfxRunner.class)
public class BAudioLocalTest {
    private static final int META_TEST_ITERS = 8;

    private static SettingsModel settings;

    @BeforeClass
    public static void setupClass() {
        try {
            Main.mainStop();
        }
        catch (MainException e) { }
        Main.mainInit(App.class, new String[]{});
    }

    /**
     * Refreshes the state of the BAudioLocal being tested.
     */
    @Before
    public void setup() throws Exception {
    	// Ensure that there is no existing metadata file
    	Main.resource.getResourceFileLocal("test_out>BAudioLocalTest>test_meta.ser").create().delete();

    	// Test with existing user settings file
        settings = new SettingsModel(Main.resource.getResourceFileLocal("settings.ser"));
    }

    /**
     * Tests whether an integer metadata value set for a BAudioLocal is correctly retrieved from RAM.
     */
    @Test
    public void test_getSetMetadataFromRAM() throws Exception {
        for (int i = 0; i<META_TEST_ITERS; i++) {
            BAudio audio = new BAudioLocal(settings, Main.resource.getResourceFileLocal("test_out>BAudioLocalTest>test_meta_" + i + ".ser"));
            audio.set(settings.TEST_VAL.to(i));
            assertEquals(new Integer(i), audio.<Integer>get(settings.TEST_VAL));
        }
    }

    /**
     * Tests whether an integer metadata value set for a BAudioLocal is correctly NOT retrieved from disk when it is NOT saved.
     */
    @Test
    public void test_getSetMetadataFromDiskUnsaved() throws Exception {
        for (int i = 0; i<META_TEST_ITERS; i++) {
            BAudio audio = new BAudioLocal(settings, Main.resource.getResourceFileLocal("test_out>BAudioLocalTest>test_meta_" + i + ".ser"));
            audio.delete();
        }
        for (int i = 0; i<META_TEST_ITERS; i++) {
            BAudio audio = new BAudioLocal(settings, Main.resource.getResourceFileLocal("test_out>BAudioLocalTest>test_meta_" + i + ".ser"));
            audio.set(settings.TEST_VAL.to(i));
        }
        for (int i = 0; i<META_TEST_ITERS; i++) {
            BAudio audio = new BAudioLocal(settings, Main.resource.getResourceFileLocal("test_out>BAudioLocalTest>test_meta_" + i + ".ser"));
            assertNotSame(new Integer(i)                    , audio.<Integer>get(settings.TEST_VAL));
            assertEquals (new Integer(settings.TEST_VAL.val), audio.<Integer>get(settings.TEST_VAL));
        }
    }

    /**
     * Tests whether an integer metadata value set for a BAudioLocal is correctly retrieved from disk when it is saved.
     */
    @Test
    public void test_getSetMetadataFromDiskSaved() throws Exception {
        for (int i = 0; i<META_TEST_ITERS; i++) {
            BAudio audio = new BAudioLocal(settings, Main.resource.getResourceFileLocal("test_out>BAudioLocalTest>test_meta_" + i + ".ser"));
            audio.delete();
        }
        for (int i = 0; i<META_TEST_ITERS; i++) {
            BAudio audio = new BAudioLocal(settings, Main.resource.getResourceFileLocal("test_out>BAudioLocalTest>test_meta_" + i + ".ser"));
            audio.set(settings.TEST_VAL.to(i));
            audio.save();
        }
        for (int i = 0; i<META_TEST_ITERS; i++) {
            BAudio audio = new BAudioLocal(settings, Main.resource.getResourceFileLocal("test_out>BAudioLocalTest>test_meta_" + i + ".ser"));
            assertEquals(new Integer(i), audio.<Integer>get(settings.TEST_VAL));
            audio.delete();
        }
    }

    /**
     * Tests whether an integer metadata value set for a BAudioLocal is correctly NOT retrieved from disk when it is saved and then deleted.
     */
    @Test
    public void test_getSetMetadataFromDiskSavedDeleted() throws Exception {
        for (int i = 0; i<META_TEST_ITERS; i++) {
            BAudio audio = new BAudioLocal(settings, Main.resource.getResourceFileLocal("test_out>BAudioLocalTest>test_meta_" + i + ".ser"));
            audio.set(settings.TEST_VAL.to(i));
            audio.save();
        }
        for (int i = 0; i<META_TEST_ITERS; i++) {
            BAudio audio = new BAudioLocal(settings, Main.resource.getResourceFileLocal("test_out>BAudioLocalTest>test_meta_" + i + ".ser"));
            audio.set(settings.TEST_VAL.to(i));
            audio.delete();
        }
        for (int i = 0; i<META_TEST_ITERS; i++) {
            BAudio audio = new BAudioLocal(settings, Main.resource.getResourceFileLocal("test_out>BAudioLocalTest>test_meta_" + i + ".ser"));
            assertNotSame(new Integer(i)                    , audio.<Integer>get(settings.TEST_VAL));
            assertEquals (new Integer(settings.TEST_VAL.val), audio.<Integer>get(settings.TEST_VAL));
        }
    }
    
    /**
     * Tests whether metadata is read properly from the test.mp3 audio file.
     */
    @Test
    public void test_readAudioMetadata() throws Exception {
        // Create test audio file
    	BAudioLocal testAudio = new BAudioLocal(settings,
			        		                    Main.resource.getResourceFileClass("test_in>test.mp3", App.class),
			                                    new BReference(settings));
        testAudio.loadMetadata(settings);
        
        // Ensure test audio file's metadata is correct
    	assertEquals(testAudio.get(settings.AUDIO_META_ARTIST), "therealergo"                 );
    	assertEquals(testAudio.get(settings.AUDIO_META_ALBUM) , "Pre-Alpha (TRE)"             );
    	assertEquals(testAudio.get(settings.AUDIO_META_TITLE) , "Proving Grounds"             );
    	assertEquals(testAudio.get(settings.AUDIO_META_TRACKN), settings.AUDIO_META_TRACKN.val);
    }
}
