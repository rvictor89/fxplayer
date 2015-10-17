package de.victorfx.fxplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Created by Ramon Victor on 17.10.2015.
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent fxplayer = FXMLLoader.load(getClass().getResource("fxml/fxplayer.fxml"));
        Scene root = new Scene(fxplayer);
        primaryStage.setScene(root);
        primaryStage.show();
    }
}
