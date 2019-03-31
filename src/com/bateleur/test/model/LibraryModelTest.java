package com.bateleur.test.model;

import java.util.Iterator;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.bateleur.app.App;
import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.model.LibraryModel;
import com.bateleur.app.model.SettingsModel;
import com.therealergo.main.Main;

import de.saxsys.javafx.test.JfxRunner;

@RunWith(JfxRunner.class)
public class LibraryModelTest {
    private static LibraryModel library;
    
    private static SettingsModel settings;
    
    @BeforeClass
    public static void setupClass() throws Exception {
        Main.mainInit(App.class, new String[]{});
    	
        settings = new SettingsModel(Main.resource.getResourceFileClass("settings.ser", App.class));
    }

    /**
     * Refreshes the state of the playbackModel being tested
     */
    @Before
    public void setup() throws Exception {
    	// Ensure that there ars no existing library files
    	Main.resource.getResourceFileClass("test_out>LibraryModelTest>library", App.class).create().delete();
    	
        library = new LibraryModel(settings, Main.resource.getResourceFolderClass("test_out>QueueModelTest>library", App.class));
    }

    //TODO: Test not actually implemented
    @Test
    public void test_library() {
    	library.filterBy( (BAudio audio) -> audio.get(settings.TEST_VAL) > 1234 );
    	library.sortBy( (BAudio a0, BAudio a1) -> a1.get(settings.TEST_VAL) - a0.get(settings.TEST_VAL) );
	
		Iterator<BAudio> audioIterator = library.iterator();
		while (audioIterator.hasNext()) {
			Main.log.log(audioIterator.next().get(settings.TEST_VAL));
		}
    }
}
