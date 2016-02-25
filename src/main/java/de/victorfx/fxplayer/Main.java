package de.victorfx.fxplayer;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Created by Ramon Victor on 17.10.2015.
 * <p>
 * Main class. Sets the primaryStage and loads the scenes.
 */
public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        ResourceBundle language = ResourceBundle.getBundle("de.victorfx.fxplayer.language", Locale.getDefault());
        primaryStage.initStyle(StageStyle.UNIFIED);
        addFavIcons(primaryStage);
        primaryStage.setResizable(true);
        primaryStage.setTitle(language.getString("appTitle"));
        URL fxml = getClass().getResource("fxml/fxplayer.fxml");
        Parent fxplayer = FXMLLoader.load(fxml, language);
        Scene root = new Scene(fxplayer);
        primaryStage.setScene(root);
        primaryStage.setMaximized(true);
        //primaryStage.setWidth(1280);
        //primaryStage.setHeight(720);
        //primaryStage.setFullScreen(true);
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(768);
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
