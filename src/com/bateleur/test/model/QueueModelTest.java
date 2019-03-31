package com.bateleur.test.model;

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
import org.junit.runner.RunWith;

@RunWith(JfxRunner.class)
public class QueueModelTest {
    private QueueModel queueModel;

    private static SettingsModel settings;
    private static LibraryModel library;
    private static BAudio testAudio;


    @BeforeClass
    public static void setupClass() throws Exception {
        Main.mainInit(App.class, new String[]{});

    	// Ensure that there are no existing library files
    	Main.resource.getResourceFileClass("test_out>QueueModelTest>library", App.class).create().delete();
    	
        settings = new SettingsModel(Main.resource.getResourceFileClass("settings.ser", App.class));
        library = new LibraryModel(settings, Main.resource.getResourceFolderClass("test_out>QueueModelTest>library", App.class));
    }

    /**
     * Refreshes the state of the playbackModel being tested
     */
    @Before
    public void setup() throws Exception {
        queueModel = new QueueModel(settings);
        
    	// Ensure that there is no existing metadata file
    	Main.resource.getResourceFileClass("test_out>QueueModelTest>test_meta.ser", App.class).create().delete();

        testAudio = new BAudioLocal(settings,
                                    Main.resource.getResourceFileClass("test_out>QueueModelTest>test_meta.ser", App.class),
                                    Main.resource.getResourceFileClass("test_in>test.mp3", App.class).getPath().toUri());
        
        queueModel.setQueue(library, testAudio);
    }
}
