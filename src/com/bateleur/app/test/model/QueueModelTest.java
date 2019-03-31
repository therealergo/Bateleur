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
import org.junit.runner.RunWith;

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

        for (int i = 0; i<8; i++) {
            BAudio audio = new BAudioLocal(settings, Main.resource.getResourceFileLocal("testLibraryModel>meta_test_" + i));
            audio.set(settings.TEST_VAL.to(1231+i));
        }

        settings = new SettingsModel(Main.resource.getResourceFileClass("settings.ser", App.class));
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


        queueModel.setQueue(library, testAudio);
    }


}
