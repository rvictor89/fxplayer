package de.victorfx.fxplayer.controller;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;

/**
 * Created by Ramon Victor on 17.10.2015.
 */
public class FXPlayercontroller {

    @FXML
    private Slider sliderTime;
    @FXML
    private Button btnStop;
    @FXML
    private Label timelabel;
    @FXML
    private Button btnPlay;
    private Media media;
    private MediaPlayer mediaplayer;
    private FileChooser fc;
    private String songpath;
    private Boolean isSliderPressed = false;


    public void play(ActionEvent event) {
        if (mediaplayer == null) {
            media = new Media(new File(songpath).toURI().toString());
            mediaplayer = new MediaPlayer(media);
            mediaplayer.currentTimeProperty().addListener(new timelabelListener());
            sliderTime.valueProperty().addListener(new sliderTimeListener());
            sliderTime.setDisable(false);
            mediaplayer.setAutoPlay(true);
            btnPlay.setText("Pause");
            btnStop.setDisable(false);
        } else if (mediaplayer.getStatus() == MediaPlayer.Status.PAUSED) {
            mediaplayer.play();
            btnPlay.setText("Pause");
        } else if (mediaplayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaplayer.pause();
            btnPlay.setText("Resume");
        } else if (mediaplayer.getStatus() == MediaPlayer.Status.STOPPED) {
            mediaplayer.play();
            btnPlay.setText("Pause");
            btnStop.setDisable(false);
        }
    }

    public void openfile(ActionEvent event) {
        fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3", "*.mp3"));
        File file = fc.showOpenDialog(null);
        if (file != null) {
            if (mediaplayer != null) {
                mediaplayer.dispose();
            }
            mediaplayer = null;
            media = null;
            songpath = file.getAbsolutePath().replace("\\", "/");
            btnPlay.setDisable(false);
            btnPlay.setText("Play");
            btnStop.setDisable(true);
        }
    }

    public void stop(ActionEvent event) {
        if (mediaplayer != null) {
            mediaplayer.stop();
            btnStop.setDisable(true);
            btnPlay.setText("Play");
        }
    }

    private class sliderTimeListener implements InvalidationListener {
        public void invalidated(Observable observable) {
            if (sliderTime.isPressed()) {
                isSliderPressed = true;
                mediaplayer.pause();
                mediaplayer.seek(new Duration(sliderTime.getValue() * 1000));
                mediaplayer.play();
                isSliderPressed = false;
            }
        }
    }

    private class timelabelListener implements InvalidationListener {
        public void invalidated(Observable observable) {
            int minutes = (int) mediaplayer.getCurrentTime().toMinutes() % 60;
            int seconds = (int) mediaplayer.getCurrentTime().toSeconds() % 60;
            int minutesDuration = (int) mediaplayer.getTotalDuration().toMinutes() % 60;
            int secondsDuration = (int) mediaplayer.getTotalDuration().toSeconds() % 60;
            timelabel.setText(String.format("%02d:%02d / %02d:%02d", minutes, seconds, minutesDuration, secondsDuration));
            if (!isSliderPressed) {
                sliderTime.setMax(mediaplayer.getTotalDuration().toSeconds());
                sliderTime.setValue(mediaplayer.getCurrentTime().toSeconds());
            }
        }
    }

}
