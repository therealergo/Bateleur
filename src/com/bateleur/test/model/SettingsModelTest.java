package com.bateleur.test.model;

import static junit.framework.TestCase.assertEquals;

import com.therealergo.main.MainException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;

import com.bateleur.app.App;
import com.bateleur.app.model.SettingsModel;
import com.therealergo.main.Main;

import de.saxsys.javafx.test.JfxRunner;

@RunWith(JfxRunner.class)
public class SettingsModelTest {
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
     * Refreshes the state of the playbackModel being tested.
     */
    @Before
    public void setup() throws Exception {
    	// Ensure that there is no existing settings file
    	Main.resource.getResourceFileLocal("test_out>SettingsModelTest>test_settings.ser").create().delete();

    	// Create settings object to be tested
        settings = new SettingsModel(Main.resource.getResourceFileLocal("test_out>SettingsModelTest>test_settings.ser"));
    }

    /**
     * Tests whether an integer setting set for a SettingsModel is correctly retrieved from RAM.
     */
    @Test
    public void test_getSetSettingFromRAM() {
    	// TEST_VAL's initial value should be its default value
        assertEquals(settings.<Integer>get(settings.TEST_VAL), settings.TEST_VAL.val);

        // Setting and then getting a setting should return the set value
    	Integer test_val = ArgumentMatchers.any(Integer.class);
    	settings.set(settings.TEST_VAL.to(test_val));
        assertEquals(settings.<Integer>get(settings.TEST_VAL), test_val);
    }

    /**
     * Tests whether an integer setting set for a SettingsModel is correctly retrieved from disk.
     */
    @Test
    public void test_getSetSettingFromDisk() throws Exception {
    	// TEST_VAL's initial value should be its default value
        assertEquals(settings.<Integer>get(settings.TEST_VAL), settings.TEST_VAL.val);

        // Setting a setting and then reloading the settings object should retain the setting
    	Integer test_val = ArgumentMatchers.any(Integer.class);
    	settings.set(settings.TEST_VAL.to(test_val));
    	settings.save();
        settings = new SettingsModel(Main.resource.getResourceFileLocal("test_out>SettingsModelTest>test_settings.ser"));
        assertEquals(settings.<Integer>get(settings.TEST_VAL), test_val);
    }
}
