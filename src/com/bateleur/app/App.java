package com.bateleur.app;

import com.bateleur.app.datatype.BAudio;
import com.bateleur.app.datatype.BAudioFile;
import com.therealergo.main.Main;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/resources/gui/views/sample.fxml"));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
        
        BAudio audio = new BAudioFile(Main.resource.getResourceFileLocal("test>meta_test_file"));
        
        // Test of BAudio
        Integer test_meta0 = audio.<Integer>getMetadata("test_meta");
        Main.log.log(test_meta0);
        
        audio.<Integer>setMetadata("test_meta", 1235);
        
        Integer test_meta1 = audio.<Integer>getMetadata("test_meta");
        Main.log.log(test_meta1);
    }

    public static void main(String[] args) {
    	Main.mainInit(App.class, args);
        launch(args);
    }
}
