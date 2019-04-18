package com.bateleur.test.datatype;

import static junit.framework.TestCase.assertEquals;

import com.therealergo.main.MainException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.bateleur.app.App;
import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.datatype.BAudioLocal;
import com.bateleur.app.model.SettingsModel;
import com.therealergo.main.Main;

import de.saxsys.javafx.test.JfxRunner;

@RunWith(JfxRunner.class)
public class BAudioLocalTest {
    private static final int META_TEST_ITERS = 8;

    private static SettingsModel settings;
    private static BAudio testAudio;

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

        // Create audio file to test with
        testAudio = new BAudioLocal(settings,
                                    Main.resource.getResourceFileLocal("test_out>BAudioLocalTest>test_meta.ser"),
                                    Main.resource.getResourceFileClass("test_in>test.mp3", App.class));
    }

    /**
     * Tests whether an integer metadata value set for a BAudioLocal is correctly retrieved from RAM.
     */
    @Test
    public void test_getSetMetadataFromRAM() throws Exception {
        for (int i = 0; i<META_TEST_ITERS; i++) {
            BAudio audio = new BAudioLocal(settings, Main.resource.getResourceFileLocal("test_out>BAudioLocalTest>test_meta_" + i + ".ser"));
            audio.set(settings.TEST_VAL.to(i));
            assertEquals(audio.<Integer>get(settings.TEST_VAL), new Integer(i));
        }
    }

    /**
     * Tests whether an integer metadata value set for a BAudioLocal is correctly retrieved from disk.
     */
    @Test
    public void test_getSetMetadataFromDisk() throws Exception {
        for (int i = 0; i<META_TEST_ITERS; i++) {
            BAudio audio = new BAudioLocal(settings, Main.resource.getResourceFileLocal("test_out>BAudioLocalTest>test_meta_" + i + ".ser"));
            audio.set(settings.TEST_VAL.to(i));
        }
        for (int i = 0; i<META_TEST_ITERS; i++) {
            BAudio audio = new BAudioLocal(settings, Main.resource.getResourceFileLocal("test_out>BAudioLocalTest>test_meta_" + i + ".ser"));
            assertEquals(audio.<Integer>get(settings.TEST_VAL), new Integer(i));
        }
    }

    /**
     * Tests whether metadata is read properly from the test.mp3 audio file.
     */
    @Test
    public void test_readAudioMetadata() {
    	assertEquals(testAudio.get(settings.AUDIO_PROP_ARTIST), "therealergo"                 );
    	assertEquals(testAudio.get(settings.AUDIO_PROP_ALBUM) , "Pre-Alpha (TRE)"             );
    	assertEquals(testAudio.get(settings.AUDIO_PROP_TITLE) , "Proving Grounds"             );
    	assertEquals(testAudio.get(settings.AUDIO_PROP_TRACKN), settings.AUDIO_PROP_TRACKN.val);
    }
}
