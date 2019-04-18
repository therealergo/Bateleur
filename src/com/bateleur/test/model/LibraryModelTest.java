package com.bateleur.test.model;

import com.therealergo.main.MainException;
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

import static org.junit.Assert.*;

@RunWith(JfxRunner.class)
public class LibraryModelTest {
    private static LibraryModel library;

    private static SettingsModel settings;

    @BeforeClass
    public static void setupClass() throws Exception {
        try {
            Main.mainStop();
        }
        catch (MainException e) { }
        Main.mainInit(App.class, new String[]{});

        settings = new SettingsModel(Main.resource.getResourceFileClass("settings.ser", App.class));
    }

    /**
     * Refreshes the state of the playbackModel being tested
     */
    @Before
    public void setup() 
    throws Exception {
    	// Ensure that there are no existing library files
    	Main.resource.getResourceFileClass("test_out>LibraryModelTest", App.class).create().delete();

        library = new LibraryModel(settings, Main.resource.getResourceFolderClass("test_out>LibraryModelTest", App.class));
    }


//    	library.sortBy( (BAudio a0, BAudio a1) -> a1.get(settings.TEST_VAL) - a0.get(settings.TEST_VAL) );


    @Test
    public void test_truePredicate_filterBy_removesOtherAudio() {
        // Given
        int originalLibrarySize = library.size();

        // When
        library.filterBy((BAudio audio) -> audio.get(settings.TEST_VAL) > 1234);

        // Then
        assertNotEquals(originalLibrarySize, library.size());
    }

    @Test
    public void test_falsePredicate_filterBy_noRemoval() {
        // Given
        int originalLibrarySize = library.size();

        // When
        library.filterBy((BAudio audio) -> audio.get(settings.TEST_VAL) < 1234);

        // Then
        assertEquals(originalLibrarySize, library.size());
    }

    @Test
    public void test_filteredLibrary_reset_originalLibrary() {
        // Given
        int originalLibrarySize = library.size();
        library.filterBy((BAudio audio) -> audio.get(settings.TEST_VAL) > 1234);
        int filteredLibrarySize = library.size();

        // When
        library.reset();

        // Then
        int resetLibrarySize = library.size();
        assertNotEquals(filteredLibrarySize, resetLibrarySize);
        assertTrue(filteredLibrarySize < resetLibrarySize);
        assertEquals(originalLibrarySize, resetLibrarySize);
    }

}
