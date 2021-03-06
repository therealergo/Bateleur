package com.bateleur.app;

import java.io.IOException;

import com.bateleur.app.controller.MasterController;
import com.bateleur.app.controller.MusicListController;
import com.bateleur.app.controller.PlaybackController;
import com.bateleur.app.model.LibraryModel;
import com.bateleur.app.model.PlaybackModel;
import com.bateleur.app.model.QueueModel;
import com.bateleur.app.model.SettingsModel;
import com.bateleur.app.view.BBackgroundCanvas;
import com.melloware.jintellitype.JIntellitype;
import com.therealergo.main.Main;
import com.therealergo.main.MainException;
import com.therealergo.main.os.EnumOS;

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
	private SettingsModel settings;
	private LibraryModel  library ;
	private PlaybackModel playback;
	private QueueModel    queue   ;
	
	@Override public void start(Stage primaryStage) throws Exception {
		settings = new SettingsModel(Main.resource.getResourceFileLocal("settings.ser"));
		library  = new LibraryModel (settings                                          );
		playback = new PlaybackModel(settings                                          );
		queue    = new QueueModel   (settings                                          );
		
		{ // Start FXML window
			FXMLLoader loader = new FXMLLoader(Main.resource.getResourceFileClass("views>MasterView.fxml", App.class).toURL());
			BuilderFactory defaultBuilderFactory = new JavaFXBuilderFactory();
			loader.setBuilderFactory(b -> {
				switch (b.getSimpleName()) {
				case "BBackgroundCanvas":
					return new Builder<BBackgroundCanvas>() {
						@Override
						public BBackgroundCanvas build() {
							return new BBackgroundCanvas(settings, playback, library);
						}
					};
				default:
					return defaultBuilderFactory.getBuilder(b);
				}
			});
			loader.setControllerFactory(c -> {
				switch (c.getSimpleName()) {
				case "MasterController":
					return new MasterController(settings, playback, library, queue);
				case "PlaybackController":
					return new PlaybackController();
				case "MusicListController":
					return new MusicListController();
				}
				throw new RuntimeException("Unable to locate controller class: " + c + "!");
			});
			Parent root = (Parent) loader.load();
			BorderlessScene scene = new BorderlessScene(primaryStage, root);
			
			scene.getStylesheets().add(Main.resource.getResourceFileClass("css>StyleGlobal.css", App.class).toURL().toExternalForm());
			
			primaryStage.setTitle("Bateleur INDEV");
			((Label) root.lookup("#topBarLabel")).setText(primaryStage.getTitle());
			
			scene.setMoveControl(root.lookup("#topBarDrag"));
			
			root.lookup("#topBarMinimize").setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override public void handle(MouseEvent event) {
					scene.minimise();
				}
			});
			
			root.lookup("#topBarMaximize").setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override public void handle(MouseEvent event) {
					scene.maximise();
				}
			});
			
			root.lookup("#topBarClose").setOnMousePressed(new EventHandler<MouseEvent>() {
				@Override public void handle(MouseEvent event) {
					if (Main.os.getOS().equals(EnumOS.WINDOWS)) {
						JIntellitype.getInstance().cleanUp();
					}
					scene.getWindow().hide();
				}
			});
			
			playback.loadFromSavedState(library);
			
			library.update();
			
			primaryStage.setScene(scene);
			primaryStage.show();
		}
	}
	
	@Override public void stop() {
		try {
			settings.save();
		} catch (IOException e) {
			Main.log.logErr(new MainException(SettingsModel.class, "Failed to save settings!", e));
		}
		try {
			library.saveAll();
		} catch (IOException e) {
			Main.log.logErr(new MainException(LibraryModel .class, "Failed to save library!" , e));
		}
		Main.mainStop();
	}

	public static void main(String[] args) {
		Main.mainInit(App.class, args);
		launch(args);
	}
}
