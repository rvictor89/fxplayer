package de.victorfx.fxplayer.controller;

import com.sun.javafx.collections.ObservableListWrapper;
import de.victorfx.fxplayer.entity.MediaEntity;
import de.victorfx.fxplayer.entity.PlaylistEntity;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
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
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Ramon Victor on 17.10.2015.
 * <p>
 * Controller for the fxplayer.xml.
 */
public class FXPlayercontroller implements Initializable {

    public static final int DOUBLECLICKTIME = 500;

    @FXML
    private Button btnNext;
    @FXML
    private Button btnBefore;
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
    private Boolean isSliderPressed = false;
    private int minutesDuration;
    private int secondsDuration;
    private MediaEntity mediaEntity;
    private MediaEntity playingMediaEntity;
    private File playlistSaveFile;
    private PlaylistEntity playlistEntity;
    private SliderVolumeListener volumeListener;
    private int clickIndex;
    private long clickTimestamp;
    private double volume;

    /**
     * Method for the "Play" button. Controls the mediaplayer and the "Play" button text.
     */
    @FXML
    private void play() {
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

    /**
     * Method for the "Open..." button. Opens a dialog to choose a mp3 song, this song is then added to the current playlist and started.
     */
    @FXML
    private void openfile() {
        fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3", "*.mp3"));
        File file = fc.showOpenDialog(null);
        if (file != null) {

            String songpath = file.getAbsolutePath().replace("\\", "/");
            media = new Media(new File(songpath).toURI().toString());
            mediaEntity = new MediaEntity(media.getSource());
            playlistList.getItems().add(0, mediaEntity);
            playlistList.getSelectionModel().select(0);
            playMediaAtIndex(0);
        }
    }

    /**
     * Method for the "Stop" button. Stops the mediaplayer and sets the button text.
     */
    @FXML
    private void stop() {
        if (mediaplayer != null) {
            mediaplayer.seek(new Duration(0.0));
            mediaplayer.stop();
            btnStop.setDisable(true);
            btnPlay.setText("Play");
        }
    }

    /**
     * Method for pressing the Timeslider.
     */
    @FXML
    private void sliderTimePressed() {
        isSliderPressed = true;
    }

    /**
     * Method for releasing the Timeslider. The mediaplayer then seeks to the selected time.
     */
    @FXML
    private void sliderTimeReleased() {
        mediaplayer.seek(new Duration(sliderTime.getValue() * 1000));
        isSliderPressed = false;
    }

    /**
     * Method for the "Click" event in the playlist. Plays the selected media.
     */
    @FXML
    private void playlistClick() {
        if (clickIndex != playlistList.getSelectionModel().getSelectedIndex()) {
            clickIndex = playlistList.getSelectionModel().getSelectedIndex();
            clickTimestamp = new Date().getTime();
        } else if (new Date().getTime() - clickTimestamp <= DOUBLECLICKTIME) {
            if (playlistList.getSelectionModel().getSelectedItem() != null) {
                playMediaAtIndex(clickIndex);
            }
        } else {
            clickTimestamp = new Date().getTime();
        }
    }

    /**
     * Plays the media object at a specific index.
     *
     * @param index the specific index
     */
    private void playMediaAtIndex(int index) {
        if (mediaplayer != null) {
            mediaplayer.dispose();
        }
        mediaplayer = null;
        media = null;
        mediaEntity = playlistList.getItems().get(index);
        media = mediaEntity.getMedia();
        mediaplayer = new MediaPlayer(mediaEntity.getMedia());
        mediaplayer.setOnReady(new PreparationWorker());
        mediaplayer.setOnEndOfMedia(new EndOfFileRunner());
    }

    /**
     * Method for the "Delete" button. Removes the selected media from the current playlist.
     */
    @FXML
    private void deleteFromList() {
        int index = playlistList.getSelectionModel().getSelectedIndex();
        playlistList.getSelectionModel().select(0);
        playlistList.getItems().remove(index);
        try {
            playlistEntity = new PlaylistEntity();
            playlistEntity.setMediaEntityList(playlistList.getItems());
            playlistSaveFile = new File("unsavedPlaylist.fxp");
            savePlaylist();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Intern method for opening a playlist from the file saved in playlistSaveFile instance.
     *
     * @throws JAXBException
     */
    private void openPlaylist() throws JAXBException {
        if (playlistSaveFile != null) {
            JAXBContext jaxbContext = JAXBContext.newInstance(PlaylistEntity.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            playlistEntity = new PlaylistEntity();
            playlistEntity = (PlaylistEntity) unmarshaller.unmarshal(playlistSaveFile);
            stop();
            if (playlistList.getItems().size() > 0) {
                playlistList.getSelectionModel().select(0);
            }
            if (playlistEntity != null && playlistEntity.getMediaEntityList() != null) {
                playlistList.setItems(new ObservableListWrapper<>(playlistEntity.getMediaEntityList()));
            }
        }
    }

    /**
     * Intern method for saving a playlist in the file saved in playlistSaveFile instance.
     *
     * @throws JAXBException
     */
    private void savePlaylist() throws JAXBException {
        JAXBContext jaxbContext = JAXBContext.newInstance(PlaylistEntity.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(playlistEntity, playlistSaveFile);
    }

    /**
     * Method for when the controller is initialized.
     *
     * @param location  location
     * @param resources resources
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        volumeListener = new SliderVolumeListener();
        playlistSaveFile = new File("unsavedPlaylist.fxp");
        volume = 1.0;
        clickIndex = 0;
        clickTimestamp = new Date().getTime();

        if (playlistSaveFile.canRead()) {
            try {
                openPlaylist();
            } catch (JAXBException e) {
                //Do nothing
            }
        }
    }

    /**
     * Method for the "Save Playlist" button. Opens a dialog for saving the XML like fxp file containing the current playlist.
     *
     * @throws JAXBException
     */
    @FXML
    private void dialogSavePlaylist() throws JAXBException {
        fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("FXPlayer Playlist", "*.fxp"));
        playlistSaveFile = fc.showSaveDialog(null);
        if (playlistSaveFile != null) {
            savePlaylist();
        }
    }

    /**
     * Method for the "Open Playlist" button. Opens a dialog for loading the XML like fxp file containing a playlist.
     *
     * @throws JAXBException
     */
    @FXML
    private void dialogOpenPlaylist() throws JAXBException {
        fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("FXPlayer Playlist", "*.fxp"));
        playlistSaveFile = fc.showOpenDialog(null);
        if (playlistSaveFile != null) {
            JAXBContext jaxbContext = JAXBContext.newInstance(PlaylistEntity.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            playlistEntity = new PlaylistEntity();
            playlistEntity = (PlaylistEntity) unmarshaller.unmarshal(playlistSaveFile);
            stop();
            if (playlistList.getItems().size() > 0) {
                playlistList.getSelectionModel().select(0);
            }
            playlistList.setItems(new ObservableListWrapper<>(playlistEntity.getMediaEntityList()));
        }
    }

    /**
     * Method for the "Add" button. Adds a new media to the playlist without playing it.
     */
    @FXML
    private void addToList() {
        fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3", "*.mp3"));
        List<File> file = fc.showOpenMultipleDialog(null);
        if (file != null && file.size() != 0) {
            for (File tmpFile : file) {
                String songpath = tmpFile.getAbsolutePath().replace("\\", "/");
                media = new Media(new File(songpath).toURI().toString());
                mediaEntity = new MediaEntity(media.getSource());
                playlistList.getItems().add(mediaEntity);
            }
            try {
                playlistEntity = new PlaylistEntity();
                playlistEntity.setMediaEntityList(playlistList.getItems());
                playlistSaveFile = new File("unsavedPlaylist.fxp");
                savePlaylist();
            } catch (JAXBException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Plays the Media before the current one.
     */
    @FXML
    private void playBeforeSong() {
        if (getPlayingIndex() - 1 < 0) {
            playMediaAtIndex(playlistList.getItems().size() - 1);
        } else {
            playMediaAtIndex(getPlayingIndex() - 1);
        }
    }

    /**
     * Plays the next Media.
     */
    @FXML
    private void playNextSong() {
        if (getPlayingIndex() + 1 >= playlistList.getItems().size()) {
            playMediaAtIndex(0);
        } else {
            playMediaAtIndex(getPlayingIndex() + 1);
        }
    }

    /**
     * Get the index of the current playing Media.
     *
     * @return index of the current playing Media
     */
    private int getPlayingIndex() {
        return playlistList.getItems().indexOf(playingMediaEntity);
    }

    /**
     * Intern InvalidationListener for the Label showing the current and total time of the media. Updates the label with the current time and the total time of the playing media.
     */
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

    /**
     * Intern InvalidationListener for the volume slider. Controls the current volume of the playing media.
     */
    private class SliderVolumeListener implements InvalidationListener {
        public void invalidated(Observable observable) {
            if (sliderVolume.isPressed()) {
                volume = sliderVolume.getValue();
                mediaplayer.setVolume(volume);
            }
        }
    }

    /**
     * Intern Runnable for the mediaplayer.setOnReady method. Controls everything about the mediaplayer, ui and the temporary playlist.
     */
    private class PreparationWorker implements Runnable {
        public void run() {
            String artist = (String) mediaplayer.getMedia().getMetadata().get("artist");
            String title = (String) mediaplayer.getMedia().getMetadata().get("title");
            String album = (String) mediaplayer.getMedia().getMetadata().get("album");
            mediaEntity.setDurationMillis(mediaplayer.getTotalDuration().toMillis());
            mediaEntity.setTitle(title);
            mediaEntity.setArtist(artist);
            mediaEntity.setAlbum(album);
            lblArtist.setText(artist != null ? artist : "");
            lblTitle.setText(title != null ? title : mediaEntity.getTitle());
            lblAlbum.setText(album != null ? album : "");

            int minutes = (int) mediaplayer.getCurrentTime().toMinutes() % 60;
            int seconds = (int) mediaplayer.getCurrentTime().toSeconds() % 60;
            minutesDuration = (int) mediaplayer.getTotalDuration().toMinutes() % 60;
            secondsDuration = (int) mediaplayer.getTotalDuration().toSeconds() % 60;
            timelabel.setText(String.format("%02d:%02d / %02d:%02d", minutes, seconds, minutesDuration, secondsDuration));
            sliderTime.setMax(mediaplayer.getTotalDuration().toSeconds());

            mediaplayer.currentTimeProperty().addListener(new TimelabelListener());
            sliderVolume.valueProperty().removeListener(volumeListener);
            sliderVolume.valueProperty().addListener(volumeListener);

            playingMediaEntity = mediaEntity;
            playlistList.getItems().set(getPlayingIndex(), mediaEntity);

            try {
                playlistEntity = new PlaylistEntity();
                playlistEntity.setMediaEntityList(playlistList.getItems());
                playlistSaveFile = new File("unsavedPlaylist.fxp");
                savePlaylist();
            } catch (JAXBException e) {
                e.printStackTrace();
            }

            sliderTime.setDisable(false);
            mediaplayer.setVolume(volume);
            mediaplayer.setAutoPlay(true);
            btnPlay.setText("Pause");
            btnPlay.setDisable(false);
            btnStop.setDisable(false);
            btnBefore.setDisable(false);
            btnNext.setDisable(false);
        }
    }

    /**
     * Intern Runnable for the mediaplayer.setOnEndOfMedia method. Jumps to the next media or stops when the last media.
     */
    private class EndOfFileRunner implements Runnable {
        public void run() {
            if (getPlayingIndex() + 1 >= playlistList.getItems().size()) {
                stop();
            } else {
                playMediaAtIndex(getPlayingIndex() + 1);
            }
        }
    }

}
