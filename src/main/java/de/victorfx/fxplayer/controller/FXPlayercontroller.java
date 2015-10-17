package de.victorfx.fxplayer.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;

import java.io.File;

/**
 * Created by Ramon Victor on 17.10.2015.
 */
public class FXPlayercontroller {

    @FXML
    private Button btnPlay;
    @FXML
    private Button filechooser;
    private Media media;
    private MediaPlayer mediaplayer;
    private FileChooser fc;
    private String songpath;

    public void play(ActionEvent event) {
        if (mediaplayer == null) {
            media = new Media(new File(songpath).toURI().toString());
            mediaplayer = new MediaPlayer(media);
            mediaplayer.setAutoPlay(true);
            btnPlay.setText("Pause");
        } else if (mediaplayer.getStatus() == MediaPlayer.Status.PAUSED) {
            mediaplayer.play();
            btnPlay.setText("Pause");
        } else if (mediaplayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaplayer.pause();
            btnPlay.setText("Resume");
        }
    }

    public void openfile(ActionEvent event) {
        fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3", "*.mp3"));
        File file = fc.showOpenDialog(null);
        songpath = file.getAbsolutePath().replace("\\", "/");
        btnPlay.setDisable(false);
    }
}
