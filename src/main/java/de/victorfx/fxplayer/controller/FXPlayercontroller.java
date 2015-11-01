package de.victorfx.fxplayer.controller;

import com.sun.javafx.collections.ObservableListWrapper;
import de.victorfx.fxplayer.entity.MediaEntity;
import de.victorfx.fxplayer.entity.PlaylistEntity;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Ramon Victor on 17.10.2015.
 */
public class FXPlayercontroller implements Initializable {

    @FXML
    private Label lblAlbum;
    @FXML
    private ListView<MediaEntity> playlistList;
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
    private SliderVolumeListener volumeListener = new SliderVolumeListener();
    private MediaEntity mediaEntity;
    private File playlistSaveFile = new File("unsavedPlaylist.fxp");
    private PlaylistEntity playlistEntity;

    @FXML
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

    @FXML
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
            mediaEntity = new MediaEntity(media.getSource());
            mediaplayer = new MediaPlayer(mediaEntity.getMedia());
            playlistList.getItems().add(0, mediaEntity);
            playlistList.getSelectionModel().select(0);
            mediaplayer.setOnReady(new PreparationWorker());
            mediaplayer.setOnEndOfMedia(new EndOfFileRunner());
        }
    }

    @FXML
    public void stop(ActionEvent event) {
        if (mediaplayer != null) {
            mediaplayer.seek(new Duration(0.0));
            mediaplayer.stop();
            btnStop.setDisable(true);
            btnPlay.setText("Play");
        }
    }

    @FXML
    public void sliderTimePressed(Event event) {
        isSliderPressed = true;
    }

    @FXML
    public void sliderTimeReleased(Event event) {
        mediaplayer.seek(new Duration(sliderTime.getValue() * 1000));
        isSliderPressed = false;
    }

    @FXML
    public void playlistClick(Event event) {
        if (playlistList.getSelectionModel().getSelectedItem() != null) {
            if (mediaplayer != null) {
                mediaplayer.dispose();
            }
            mediaplayer = null;
            media = null;
            mediaEntity = playlistList.getSelectionModel().getSelectedItem();
            media = mediaEntity.getMedia();
            mediaplayer = new MediaPlayer(mediaEntity.getMedia());
            mediaplayer.setOnReady(new PreparationWorker());
            mediaplayer.setOnEndOfMedia(new EndOfFileRunner());
        }
    }

    @FXML
    public void deleteFromList(ActionEvent actionEvent) {
        int index = playlistList.getSelectionModel().getSelectedIndex();
        playlistList.getSelectionModel().select(0);
        playlistList.getItems().remove(index);
    }

    private void openPlaylist(ActionEvent actionEvent) throws JAXBException {
        if (playlistSaveFile != null) {
            JAXBContext jaxbContext = JAXBContext.newInstance(PlaylistEntity.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            playlistEntity = new PlaylistEntity();
            playlistEntity = (PlaylistEntity) unmarshaller.unmarshal(playlistSaveFile);
            stop(null);
            if (playlistList.getItems().size() > 0) {
                playlistList.getSelectionModel().select(0);
            }
            playlistList.setItems(new ObservableListWrapper<>(playlistEntity.getMediaEntityList()));
        }
    }

    private void savePlaylist(ActionEvent actionEvent) throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(PlaylistEntity.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(playlistEntity, playlistSaveFile);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (playlistSaveFile.canRead()) {
            try {
                openPlaylist(null);
            } catch (JAXBException e) {
                //Do nothing
            }
        }
    }

    @FXML
    public void dialogSavePlaylist(ActionEvent actionEvent) throws JAXBException {
        fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("FXPlayer Playlist", "*.fxp"));
        playlistSaveFile = fc.showSaveDialog(null);
        if (playlistSaveFile != null) {
            savePlaylist(null);
        }
    }

    @FXML
    public void dialogOpenPlaylist(ActionEvent actionEvent) throws JAXBException {
        fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("FXPlayer Playlist", "*.fxp"));
        playlistSaveFile = fc.showOpenDialog(null);
        if (playlistSaveFile != null) {
            JAXBContext jaxbContext = JAXBContext.newInstance(PlaylistEntity.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            playlistEntity = new PlaylistEntity();
            playlistEntity = (PlaylistEntity) unmarshaller.unmarshal(playlistSaveFile);
            stop(null);
            if (playlistList.getItems().size() > 0) {
                playlistList.getSelectionModel().select(0);
            }
            playlistList.setItems(new ObservableListWrapper<>(playlistEntity.getMediaEntityList()));
        }
    }

    private class TimelabelListener implements InvalidationListener {
        public void invalidated(Observable observable) {
            int minutes = (int) mediaplayer.getCurrentTime().toMinutes() % 60;
            int seconds = (int) mediaplayer.getCurrentTime().toSeconds() % 60;
            timelabel.setText(String.format("%02d:%02d / %02d:%02d", minutes, seconds, minutesDuration, secondsDuration));
            if (!isSliderPressed) {
                sliderTime.setValue(mediaplayer.getCurrentTime().toSeconds());
            }
        }
    }

    private class SliderVolumeListener implements InvalidationListener {
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
            String album = (String) mediaplayer.getMedia().getMetadata().get("album");
            mediaEntity.setDurationMillis(mediaplayer.getTotalDuration().toMillis());
            mediaEntity.setTitle(title);
            mediaEntity.setArtist(artist);
            mediaEntity.setAlbum(album);
            lblArtist.setText(artist != null ? artist : "NA");
            lblTitle.setText(title != null ? title : "NA");
            lblAlbum.setText(album != null ? album : "NA");

            int minutes = (int) mediaplayer.getCurrentTime().toMinutes() % 60;
            int seconds = (int) mediaplayer.getCurrentTime().toSeconds() % 60;
            minutesDuration = (int) mediaplayer.getTotalDuration().toMinutes() % 60;
            secondsDuration = (int) mediaplayer.getTotalDuration().toSeconds() % 60;
            timelabel.setText(String.format("%02d:%02d / %02d:%02d", minutes, seconds, minutesDuration, secondsDuration));
            sliderTime.setMax(mediaplayer.getTotalDuration().toSeconds());

            mediaplayer.currentTimeProperty().addListener(new TimelabelListener());
            sliderVolume.valueProperty().removeListener(volumeListener);
            sliderVolume.valueProperty().addListener(volumeListener);

            int index = playlistList.getSelectionModel().getSelectedIndex();
            playlistList.getItems().set(index, mediaEntity);

            try {
                playlistEntity = new PlaylistEntity();
                playlistEntity.setMediaEntityList(playlistList.getItems());
                playlistSaveFile = new File("unsavedPlaylist.fxp");
                savePlaylist(null);
            } catch (JAXBException e) {
                e.printStackTrace();
            }

            sliderTime.setDisable(false);
            mediaplayer.setAutoPlay(true);
            btnPlay.setText("Pause");
            btnPlay.setDisable(false);
            btnStop.setDisable(false);
        }
    }

    private class EndOfFileRunner implements Runnable {
        public void run() {
            int index = playlistList.getSelectionModel().getSelectedIndex();
            if (index + 1 == playlistList.getItems().size()) {
                stop(null);
            } else {
                playlistList.getSelectionModel().select(index + 1);
                playlistClick(null);
            }
        }
    }

}
