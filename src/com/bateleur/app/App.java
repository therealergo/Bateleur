package com.bateleur.app;

import java.util.Iterator;

import com.bateleur.app.controller.PlaybackController;
import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.datatype.BAudioFile;
import com.bateleur.app.model.LibraryModel;
import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.SettingsModel;
import com.therealergo.main.Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
    	SettingsModel settingsModel = new SettingsModel(Main.resource.getResourceFileClass("settings.ser", App.class));
    	PlaybackModel playbackModel = new PlaybackModel(settingsModel);
    	
    	{ // Test of SettingsModel
		    Main.log.log(settingsModel.<Integer>getSetting("test_setting", 1));
		    settingsModel.<Integer>setSetting("test_setting", (int)(System.nanoTime()%8));
		    Main.log.log(settingsModel.<Integer>getSetting("test_setting", 1));
    	}
    	
        { // Test of BAudio
        	BAudio audio = new BAudioFile(settingsModel, Main.resource.getResourceFileLocal("testBAudio>meta_test_file"));
		    
		    Integer test_meta0 = audio.<Integer>getMetadata("test_meta");
		    Main.log.log(test_meta0);
		    
		    audio.<Integer>setMetadata("test_meta", 123);
		    
		    Integer test_meta1 = audio.<Integer>getMetadata("test_meta");
		    Main.log.log(test_meta1);
        }
        
        { // Test of LibraryModel
        	for (int i = 0; i<8; i++) {
            	BAudio audio = new BAudioFile(settingsModel, Main.resource.getResourceFileLocal("testLibraryModel>meta_test_" + i));
    		    audio.<Integer>setMetadata("test_meta", 1230 + i);
        	}
		    
        	LibraryModel libraryModel = new LibraryModel(settingsModel, Main.resource.getResourceLocal("testLibraryModel"));
        	
        	libraryModel.filterBy( (BAudio audio) -> audio.<Integer>getMetadata("test_meta") > 1234 );
        	libraryModel.sortBy( (BAudio a0, BAudio a1) -> a1.<Integer>getMetadata("test_meta") - a0.<Integer>getMetadata("test_meta") );
        	
        	Iterator<BAudio> audioIterator = libraryModel.iterator();
        	while (audioIterator.hasNext()) {
        		Main.log.log(audioIterator.next().<Integer>getMetadata("test_meta"));
        	}
        }
        
        { // Test of PlaybackModel
        	BAudio audio = new BAudioFile(settingsModel, Main.resource.getResourceFileLocal("testPlaybackModel>__meta_test"), Main.resource.getResourceFileLocal("testPlaybackModel>test.mp3").getFullPath().toUri());
        	playbackModel.loadAudio(audio);
        	playbackModel.play(0);
        }
        
        { // Start FXML window
	    	FXMLLoader loader = new FXMLLoader(getClass().getResource("/resources/gui/views/sample.fxml"));
	    	loader.setControllerFactory(c -> {
	    		switch (c.getSimpleName()) {
		    		case "PlaybackController":
		    			return new PlaybackController(settingsModel, playbackModel, null);
	    		}
	    		throw new RuntimeException("Unable to locate controller class: " + c + "!");
	    	});
	    	Parent root = (Parent)loader.load();
	        primaryStage.setTitle("Bateleur INDEV");
	        primaryStage.setScene(new Scene(root, 300, 275));
	        primaryStage.show();
        }
    }

    public static void main(String[] args) {
    	Main.mainInit(App.class, args);
        launch(args);
    }
}
