package de.victorfx.fxplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
        addFavIcons(primaryStage);
        primaryStage.setResizable(true);
        primaryStage.setTitle("FXPlayer 1.0-SNAPSHOT");
        Parent fxplayer = FXMLLoader.load(getClass().getResource("fxml/fxplayer.fxml"));
        Scene root = new Scene(fxplayer);
        primaryStage.setScene(root);
        primaryStage.show();
    }

    private void addFavIcons(Stage primaryStage) {
        primaryStage.getIcons().add(new Image(this.getClass().getResource("images/logo/FXPlayerLogo_16.png").toString()));
        primaryStage.getIcons().add(new Image(this.getClass().getResource("images/logo/FXPlayerLogo_32.png").toString()));
        primaryStage.getIcons().add(new Image(this.getClass().getResource("images/logo/FXPlayerLogo_64.png").toString()));
        primaryStage.getIcons().add(new Image(this.getClass().getResource("images/logo/FXPlayerLogo_128.png").toString()));
        primaryStage.getIcons().add(new Image(this.getClass().getResource("images/logo/FXPlayerLogo_256.png").toString()));
        primaryStage.getIcons().add(new Image(this.getClass().getResource("images/logo/FXPlayerLogo_512.png").toString()));
        primaryStage.getIcons().add(new Image(this.getClass().getResource("images/logo/FXPlayerLogo_1024.png").toString()));
    }
}
