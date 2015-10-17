package de.victorfx.fxplayer.controller;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.io.File;

/**
 * Created by Ramon Victor on 17.10.2015.
 */
public class FXPlayercontroller {

    public static final String SONGPATH = "D:/Eigene Musik/Test.mp3";

    public Button btnPlay;
    private Media media;
    private MediaPlayer mediaplayer;

    public void play(ActionEvent event) {
        media = new Media(new File(SONGPATH).toURI().toString());
        mediaplayer = new MediaPlayer(media);
        mediaplayer.setAutoPlay(true);
    }
}
