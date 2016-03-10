package de.victorfx.fxplayer.controller;

import com.sun.javafx.collections.ObservableListWrapper;
import de.victorfx.fxplayer.entity.MediaEntity;
import de.victorfx.fxplayer.entity.PlaylistEntity;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.media.AudioSpectrumListener;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import javafx.util.Duration;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Ramon Victor on 17.10.2015.
 * <p>
 * Controller for the fxplayer.xml.
 */
public class FXPlayercontroller implements Initializable {

    private static final int BANDS = 32;
    private static final double INTERVAL = 0.015;
    private static final double DROPDOWN = 0.25;
    private static final int DOUBLECLICKTIME = 500;
    private final DataFormat dataFormat = new DataFormat("MediaEntity");
    @FXML private TitledPane titledPaneVisualizer;
    @FXML private Label fps;
    @FXML private MediaView videoView;
    @FXML private AreaChart<String, Number> spektrum;
    @FXML private TableColumn<Object, Object> tableColTitle;
    @FXML private TableColumn<Object, Object> tableColArtist;
    @FXML private TableColumn<Object, Object> tableColAlbum;
    @FXML private TableColumn<MediaEntity, Double> tableColDuration;
    @FXML private Label lblVolume;
    @FXML private Button btnNext;
    @FXML private Button btnBefore;
    @FXML private Label lblAlbum;
    @FXML private TableView<MediaEntity> playlistList;
    @FXML private Label lblTitle;
    @FXML private Label lblArtist;
    @FXML private Slider sliderVolume;
    @FXML private Slider sliderTime;
    @FXML private Button btnStop;
    @FXML private Label timelabel;
    @FXML private Button btnPlay;
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
    private TimeSliderListener timeSliderListener;
    private int clickIndex;
    private int dropIndex;
    private long clickTimestamp;
    private double volume;
    private ResourceBundle language;
    private XYChart.Data[] series1Data;

    /**
     * Method for the "Play" button. Controls the mediaplayer and the "Play" button text.
     */
    @FXML
    private void play() {
        if (mediaplayer.getStatus() == MediaPlayer.Status.PAUSED) {
            mediaplayer.play();
            btnPlay.setText(language.getString("pause"));
        } else if (mediaplayer.getStatus() == MediaPlayer.Status.PLAYING) {
            mediaplayer.pause();
            btnPlay.setText(language.getString("resume"));
        } else if (mediaplayer.getStatus() == MediaPlayer.Status.STOPPED) {
            mediaplayer.play();
            btnPlay.setText(language.getString("pause"));
            btnStop.setDisable(false);
        }
    }

    /**
     * Method for the "Open..." button. Opens a dialog to choose a mp3 song, this song is then added to the current
     * playlist and started.
     */
    @FXML
    private void openfile() {
        fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP3", "*.mp3"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("WAV", "*.wav"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("MP4", "*.mp4"));
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("FLV", "*.flv"));
        File file = fc.showOpenDialog(null);
        if (file != null) {
            String songpath = file.getAbsolutePath().replace("\\", "/");
            media = new Media(new File(songpath).toURI().toString());
            mediaEntity = new MediaEntity(media.getSource());
            playlistList.getItems().add(0, mediaEntity);
            generateMediaMetadata(mediaEntity);
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
            btnPlay.setText(language.getString("play"));
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
        int minutes = (int) mediaplayer.getCurrentTime().toMinutes() % 60;
        int seconds = (int) mediaplayer.getCurrentTime().toSeconds() % 60;
        minutesDuration = (int) mediaplayer.getTotalDuration().toMinutes() % 60;
        secondsDuration = (int) mediaplayer.getTotalDuration().toSeconds() % 60;
        timelabel.setText(String.format("%02d:%02d / %02d:%02d", minutes, seconds, minutesDuration, secondsDuration));
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

        language = resources;

        XYChart.Series<String, Number> series1 = new XYChart.Series<>();
        series1Data = new XYChart.Data[BANDS + 2];
        for (int i = 0; i < series1Data.length; i++) {
            series1Data[i] = new XYChart.Data<>(Integer.toString(i + 1), 0);
            //noinspection unchecked
            series1.getData().add(series1Data[i]);
        }
        spektrum.getData().add(series1);

        tableColDuration.setCellValueFactory(new PropertyValueFactory<>("niceDuration"));
        tableColAlbum.setCellValueFactory(new PropertyValueFactory<>("album"));
        tableColArtist.setCellValueFactory(new PropertyValueFactory<>("artist"));
        tableColTitle.setCellValueFactory(new PropertyValueFactory<>("title"));

        volumeListener = new SliderVolumeListener();
        sliderVolume.valueProperty().addListener(volumeListener);
        timeSliderListener = new TimeSliderListener();
        playlistSaveFile = new File("unsavedPlaylist.fxp");
        volume = 1.0;
        lblVolume.setText(String.format("%02d %%", (int) (volume * 100)));
        clickIndex = 0;
        clickTimestamp = new Date().getTime();

        if (playlistSaveFile.canRead()) {
            try {
                openPlaylist();
            } catch (JAXBException e) {
                //Do nothing
            }
        }

        playlistList.setRowFactory(new MediaListCallback());
        AnimationTimer timer = new FPSCounter();
        timer.start();
    }

    /**
     * Method for the "Save Playlist" button. Opens a dialog for saving the XML like fxp file containing the current
     * playlist.
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
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("WAV", "*.wav"));
        List<File> file = fc.showOpenMultipleDialog(null);
        if (file != null && file.size() != 0) {
            for (File tmpFile : file) {
                String songpath = tmpFile.getAbsolutePath().replace("\\", "/");
                media = new Media(new File(songpath).toURI().toString());
                mediaEntity = new MediaEntity(media.getSource());
                playlistList.getItems().add(mediaEntity);
                generateMediaMetadata(mediaEntity);
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
     * Closes the application.
     */
    @FXML
    private void closeApplication() {
        Platform.exit();
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
     * Get the index of an specific mediaEntity.
     *
     * @param mediaEntity the specific mediaEntity.
     * @return index of the specific mediaEntity.
     */
    private int getIndexOfMediaInPlaylist(MediaEntity mediaEntity) {
        return playlistList.getItems().indexOf(mediaEntity);
    }

    /**
     * Generates the Metadata for a specific mediaEntity.
     *
     * @param mediaEntity the specific mediaEntity.
     */
    private void generateMediaMetadata(MediaEntity mediaEntity) {
        if (playlistList.getItems().contains(mediaEntity)) {
            MediaPlayer mediaplayer = new MediaPlayer(mediaEntity.getMedia());
            mediaplayer.setOnReady(new MetadataWorker(mediaEntity, mediaplayer));
        }
    }

    /**
     * Creates a float[] filled with the given value.
     *
     * @param size      the size of the float[]
     * @param fillValue the value which will be filled in the float[]
     * @return the filled float[]
     */
    private float[] createFilledBuffer(int size, float fillValue) {
        float[] floats = new float[size];
        Arrays.fill(floats, fillValue);
        return floats;
    }

    /**
     * Intern InvalidationListener for the Label showing the current and total time of the media. Updates the label
     * with the current time and the total time of the playing media.
     */
    private class TimelabelListener implements InvalidationListener {
        public void invalidated(Observable observable) {
            if (!isSliderPressed) {
                int minutes = (int) mediaplayer.getCurrentTime().toMinutes() % 60;
                int seconds = (int) mediaplayer.getCurrentTime().toSeconds() % 60;
                timelabel.setText(String.format("%02d:%02d / %02d:%02d", minutes, seconds, minutesDuration,
                        secondsDuration));
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
                lblVolume.setText(String.format("%01d %%", (int) (volume * 100)));
                if (mediaplayer != null) {
                    mediaplayer.setVolume(volume);
                }
            }
        }
    }

    /**
     * Intern InvalidationListener for the time slider. Controls the shown time when sliding.
     */
    private class TimeSliderListener implements InvalidationListener {
        public void invalidated(Observable observable) {
            if (isSliderPressed) {
                int minutes = (int) sliderTime.getValue() / 60;
                int seconds = (int) sliderTime.getValue() % 60;
                timelabel.setText(String.format("%02d:%02d / %02d:%02d", minutes, seconds, minutesDuration,
                        secondsDuration));
            }
        }
    }

    /**
     * Intern Runnable for the mediaplayer.setOnReady method. Controls everything about the mediaplayer, ui and the
     * temporary playlist.
     */
    private class PreparationWorker implements Runnable {
        public void run() {

            //videoView.setMediaPlayer(mediaplayer);

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
            timelabel.setText(String.format("%02d:%02d / %02d:%02d", minutes, seconds, minutesDuration,
                    secondsDuration));
            sliderTime.setMax(mediaplayer.getTotalDuration().toSeconds());

            mediaplayer.currentTimeProperty().addListener(new TimelabelListener());
            mediaplayer.setAudioSpectrumListener(new SpektrumListener());
            mediaplayer.setAudioSpectrumNumBands(BANDS);
            //mediaplayer.setAudioSpectrumInterval(INTERVAL);
            sliderVolume.valueProperty().removeListener(volumeListener);
            sliderVolume.valueProperty().addListener(volumeListener);
            sliderTime.valueProperty().removeListener(timeSliderListener);
            sliderTime.valueProperty().addListener(timeSliderListener);

            if (playingMediaEntity != null) {
                playingMediaEntity.setPlaying(false);
            }
            playingMediaEntity = mediaEntity;
            playingMediaEntity.setPlaying(true);
            playlistList.getItems().set(getPlayingIndex(), mediaEntity);
            playlistList.refresh();

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
            btnPlay.setText(language.getString("pause"));
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

    /**
     * Intern Runnable for the generateMediaMetadata method.
     */
    private class MetadataWorker implements Runnable {
        MediaEntity mediaEntity;
        MediaPlayer mediaPlayer;

        public MetadataWorker(MediaEntity mediaEntity, MediaPlayer mediaPlayer) {
            this.mediaEntity = mediaEntity;
            this.mediaPlayer = mediaPlayer;
        }

        public void run() {
            String artist = (String) mediaPlayer.getMedia().getMetadata().get("artist");
            String title = (String) mediaPlayer.getMedia().getMetadata().get("title");
            String album = (String) mediaPlayer.getMedia().getMetadata().get("album");
            mediaEntity.setDurationMillis(mediaPlayer.getTotalDuration().toMillis());
            mediaEntity.setTitle(title);
            mediaEntity.setArtist(artist);
            mediaEntity.setAlbum(album);

            if (playlistList.getItems().contains(mediaEntity)) {
                int indexOfMediaInPlaylist = getIndexOfMediaInPlaylist(mediaEntity);
                playlistList.getItems().set(indexOfMediaInPlaylist, mediaEntity);

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
    }

    /**
     * Intern Callback for the rendering of the rows of the playlist. Handles Drag-and-Drop.
     */
    private class MediaListCallback implements Callback<TableView<MediaEntity>, TableRow<MediaEntity>> {

        @Override
        public TableRow<MediaEntity> call(TableView<MediaEntity> param) {
            TableRow<MediaEntity> row = new TableRow<MediaEntity>() {
                @Override
                protected void updateItem(MediaEntity item, boolean empty) {
                    if (item != null) {
                        if (item.isPlaying()) {
                            getStyleClass().add("media-playing");
                        }
                    }
                    super.updateItem(item, empty);
                }
            };

            row.setOnDragDetected(event -> {
                if (!row.isEmpty()) {
                    Dragboard db = row.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent cc = new ClipboardContent();
                    cc.put(dataFormat, row.getItem());
                    db.setContent(cc);
                    playlistList.getItems().remove(row.getItem());
                }
            });

            row.setOnDragOver(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(dataFormat)) {
                    event.acceptTransferModes(TransferMode.MOVE);
                    playlistList.getSelectionModel().select(row.getIndex());
                }
            });

            row.setOnDragDone(event -> {
                playlistList.getSelectionModel().select(dropIndex);
                try {
                    playlistEntity = new PlaylistEntity();
                    playlistEntity.setMediaEntityList(playlistList.getItems());
                    playlistSaveFile = new File("unsavedPlaylist.fxp");
                    savePlaylist();
                } catch (JAXBException e) {
                    e.printStackTrace();
                }
            });

            row.setOnDragDropped(event -> {
                Dragboard db = event.getDragboard();
                if (db.hasContent(dataFormat)) {
                    MediaEntity mediaEntity = (MediaEntity) db.getContent(dataFormat);
                    dropIndex = row.getIndex();
                    if (dropIndex > playlistList.getItems().size()) {
                        dropIndex = playlistList.getItems().size();
                    }
                    playlistList.getItems().add(dropIndex, mediaEntity);
                    event.setDropCompleted(true);
                } else {
                    event.setDropCompleted(false);
                }
            });

            return row;
        }
    }

    /**
     * Intern AudioSpectrumListener for the audio visualization.
     */
    private class SpektrumListener implements AudioSpectrumListener {
        float[] buffer = createFilledBuffer(BANDS, mediaplayer.getAudioSpectrumThreshold());

        @Override
        public void spectrumDataUpdate(double timestamp, double duration, float[] magnitudes, float[] phases) {
            //noinspection unchecked
            series1Data[0].setYValue(0);
            //noinspection unchecked
            series1Data[BANDS + 1].setYValue(0);
            for (int i = 0; i < magnitudes.length; i++) {
                if (magnitudes[i] >= buffer[i]) {
                    buffer[i] = magnitudes[i];
                    //noinspection unchecked
                    series1Data[i + 1].setYValue(magnitudes[i] - mediaplayer.getAudioSpectrumThreshold());
                } else {
                    //noinspection unchecked
                    series1Data[i + 1].setYValue(buffer[i] - mediaplayer.getAudioSpectrumThreshold());
                    buffer[i] -= DROPDOWN;
                }
            }
        }
    }

    /**
     * Intern AnimationTimer for computing the frames per second rate.
     */
    private class FPSCounter extends AnimationTimer {

        long oldNowTime = 0;
        long currentFramerate = 0;
        long tmpTime = 0;

        @Override
        public void handle(long now) {
            long elapsedTime = now - oldNowTime;
            long tmpElapsedTime = now - tmpTime;
            currentFramerate = 1_000_000_000 / elapsedTime;
            if (tmpElapsedTime >= 1_000_000_000) {
                fps.setText(String.valueOf(currentFramerate));
                tmpTime = now;
            }
            if (titledPaneVisualizer.isExpanded() && mediaplayer != null) {
                mediaplayer.setAudioSpectrumInterval(INTERVAL);
            } else if (!titledPaneVisualizer.isExpanded() && mediaplayer != null) {
                mediaplayer.setAudioSpectrumInterval(600);
            }
            oldNowTime = now;
        }
    }

}
