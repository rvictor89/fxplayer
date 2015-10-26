package de.victorfx.fxplayer.controller;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Tooltip;
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
    private Label lblTitle;
    @FXML
    private Label lblArtist;
    @FXML
    private Slider sliderVolume;
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
    private int minutesDuration;
    private int secondsDuration;


    public void play(ActionEvent event) {
        if (mediaplayer.getStatus() == MediaPlayer.Status.PAUSED) {
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
            media = new Media(new File(songpath).toURI().toString());
            mediaplayer = new MediaPlayer(media);
            mediaplayer.setOnReady(new PreparationWorker());
            mediaplayer.setOnEndOfMedia(() -> stop(null));
        }
    }

    public void stop(ActionEvent event) {
        if (mediaplayer != null) {
            mediaplayer.seek(new Duration(0.0));
            mediaplayer.stop();
            btnStop.setDisable(true);
            btnPlay.setText("Play");
        }
    }

    public void sliderTimePressed(Event event) {
        isSliderPressed = true;
    }

    public void sliderTimeReleased(Event event) {
        mediaplayer.seek(new Duration(sliderTime.getValue() * 1000));
        isSliderPressed = false;
    }

    private class timelabelListener implements InvalidationListener {
        public void invalidated(Observable observable) {
            int minutes = (int) mediaplayer.getCurrentTime().toMinutes() % 60;
            int seconds = (int) mediaplayer.getCurrentTime().toSeconds() % 60;
            timelabel.setText(String.format("%02d:%02d / %02d:%02d", minutes, seconds, minutesDuration, secondsDuration));
            if (!isSliderPressed) {
                sliderTime.setValue(mediaplayer.getCurrentTime().toSeconds());
            }
        }
    }

    private class sliderVolumeListener implements InvalidationListener {
        public void invalidated(Observable observable) {
            if (sliderVolume.isPressed()) {
                mediaplayer.setVolume(sliderVolume.getValue());
            }
        }
    }

    private class PreparationWorker implements Runnable {
        public void run() {
            String artist = (String) mediaplayer.getMedia().getMetadata().get("artist");
            String title = (String) mediaplayer.getMedia().getMetadata().get("title");
            lblArtist.setText(artist != null ? artist : "NA");
            lblTitle.setText(title != null ? title : "NA");

            int minutes = (int) mediaplayer.getCurrentTime().toMinutes() % 60;
            int seconds = (int) mediaplayer.getCurrentTime().toSeconds() % 60;
            minutesDuration = (int) mediaplayer.getTotalDuration().toMinutes() % 60;
            secondsDuration = (int) mediaplayer.getTotalDuration().toSeconds() % 60;
            timelabel.setText(String.format("%02d:%02d / %02d:%02d", minutes, seconds, minutesDuration, secondsDuration));
            sliderTime.setMax(mediaplayer.getTotalDuration().toSeconds());

            mediaplayer.currentTimeProperty().addListener(new timelabelListener());
            sliderVolume.valueProperty().addListener(new sliderVolumeListener());

            sliderTime.setDisable(false);
            mediaplayer.setAutoPlay(true);
            btnPlay.setText("Pause");
            btnPlay.setDisable(false);
            btnStop.setDisable(false);
        }
    }
}
