package com.bateleur.app;

import java.util.Iterator;

import com.bateleur.app.controller.MusicListController;
import com.bateleur.app.controller.PlaybackController;
import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.datatype.BAudioLocal;
import com.bateleur.app.model.LibraryModel;
import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.QueueModel;
import com.bateleur.app.model.SettingsModel;
import com.bateleur.app.view.BBackgroundCanvas;
import com.therealergo.main.Main;

import borderless.BorderlessScene;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Builder;
import javafx.util.BuilderFactory;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
    	SettingsModel settings = new SettingsModel(Main.resource.getResourceFileClass("settings.ser", App.class));
    	PlaybackModel playback = new PlaybackModel(settings);
    	LibraryModel  library  = new LibraryModel (settings, Main.resource.getResourceFolderClass("library", App.class));
//    	PlaylistModel playlist = new PlaylistModel(settings);
    	QueueModel    queue    = new QueueModel   (settings);

    	{ // Test of SettingsModel
		    Main.log.log(settings.get(settings.TEST_VAL));
		                 settings.set(settings.TEST_VAL.to((int)(System.nanoTime()%8)) );
		    Main.log.log(settings.get(settings.TEST_VAL));
    	}

        { // Test of BAudio
        	BAudio audio = new BAudioLocal(settings, Main.resource.getResourceFileLocal("testBAudio>meta_test_file"));

		    Integer test_meta0 = audio.get(settings.TEST_VAL);
		    Main.log.log(test_meta0);

		    audio.set(settings.TEST_VAL.to(123));

		    Integer test_meta1 = audio.get(settings.TEST_VAL);
		    Main.log.log(test_meta1);
        }

        { // Test of LibraryModel
        	for (int i = 0; i<8; i++) {
            	BAudio audio = new BAudioLocal(settings, Main.resource.getResourceFileLocal("testLibraryModel>meta_test_" + i));
    		    audio.set(settings.TEST_VAL.to(1230+i));
        	}

        	LibraryModel libraryTest = new LibraryModel(settings, Main.resource.getResourceFolderLocal("testLibraryModel"));

        	libraryTest.filterBy( (BAudio audio) -> audio.get(settings.TEST_VAL) > 1234 );
        	libraryTest.sortBy( (BAudio a0, BAudio a1) -> a1.get(settings.TEST_VAL) - a0.get(settings.TEST_VAL) );

        	Iterator<BAudio> audioIterator = libraryTest.iterator();
        	while (audioIterator.hasNext()) {
        		Main.log.log(audioIterator.next().get(settings.TEST_VAL));
        	}
        }

        { // Test of PlaybackModel
        	BAudio audio = new BAudioLocal(settings, Main.resource.getResourceFileLocal("testPlaybackModel>__meta_test"), Main.resource.getResourceFileLocal("testPlaybackModel>test.mp3").getPath().toUri());
        	playback.loadAudio(audio, 0);
        	playback.play(0);
        }

        library.update();
        library.sortBy( (BAudio audio1, BAudio audio2) -> audio2.get(settings.AUDIO_PROP_TITLE).compareTo(audio1.get(settings.AUDIO_PROP_TITLE)) );
        playback.loadAudio(library.iterator().next(), 0);
        playback.play(0);
        queue.setQueue(library, library.iterator().next());

        { // Start FXML window
			FXMLLoader loader = new FXMLLoader(Main.resource.getResourceFileClass("view.fxml", App.class).getPath().toUri().toURL());
	    	BuilderFactory defaultBuilderFactory = new JavaFXBuilderFactory();
	    	loader.setBuilderFactory(b -> {
	    		switch (b.getSimpleName()) {
		    		case "BBackgroundCanvas":
		    			return new Builder<BBackgroundCanvas>(){
							@Override
							public BBackgroundCanvas build() {
								return new BBackgroundCanvas(settings, playback);
							}
		    			};
		    		default:
		    			return defaultBuilderFactory.getBuilder(b);
	    		}
	    	});
	    	loader.setControllerFactory(c -> {
	    		switch (c.getSimpleName()) {
		    		case "PlaybackController":
		    			return new PlaybackController(settings, playback, queue);
		    		case "MusicListController":
		    			return new MusicListController(settings, library, playback, queue);
	    		}
	    		throw new RuntimeException("Unable to locate controller class: " + c + "!");
	    	});
	    	Parent root = (Parent)loader.load();
	        BorderlessScene scene = new BorderlessScene(primaryStage, root);

	        primaryStage.setTitle("Bateleur INDEV");
	        ((Label)root.lookup("#topBarLabel")).setText(primaryStage.getTitle());

	        scene.setMoveControl(root.lookup("#topBarDrag"));

	        root.lookup("#topBarMinimize").setOnMousePressed(new EventHandler<MouseEvent>(){
	        	@Override public void handle(MouseEvent event) {
					scene.minimise();
				}
	        });

	        root.lookup("#topBarMaximize").setOnMousePressed(new EventHandler<MouseEvent>(){
	        	@Override public void handle(MouseEvent event) {
					scene.maximise();
				}
	        });

	        root.lookup("#topBarClose").setOnMousePressed(new EventHandler<MouseEvent>(){
	        	@Override public void handle(MouseEvent event) {
	        		scene.getWindow().hide();
				}
	        });

	        primaryStage.setScene(scene);
	        primaryStage.show();
        }
    }

    public static void main(String[] args) {
    	Main.mainInit(App.class, args);
        launch(args);
    }
}
