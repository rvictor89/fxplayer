package de.victorfx.fxplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * Created by Ramon Victor on 17.10.2015.
 *
 * Main class. Sets the primaryStage and loads the scenes.
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.initStyle(StageStyle.UNIFIED);
        primaryStage.setResizable(true);
        primaryStage.setTitle("FXPlayer 1.0-SNAPSHOT");
        Parent fxplayer = FXMLLoader.load(getClass().getResource("fxml/fxplayer.fxml"));
        Scene root = new Scene(fxplayer);
        primaryStage.setScene(root);
        primaryStage.show();
    }
}
