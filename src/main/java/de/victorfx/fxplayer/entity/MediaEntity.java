package de.victorfx.fxplayer.entity;

import javafx.scene.media.Media;
import javafx.util.Duration;

import java.io.File;
import java.io.Serializable;

/**
 * Created by Ramon Victor on 26.10.2015.
 *
 * The MediaEntity containing Media attributes for saving into the fxp file.
 */
public class MediaEntity implements Serializable {

    private String source;
    private String title;
    private String artist;
    private String album;
    private double durationMillis;

    public MediaEntity() {
    }

    public MediaEntity(String source) {
        this.source = source;
    }

    public double getDurationMillis() {
        return durationMillis;
    }

    public void setDurationMillis(double durationMillis) {
        this.durationMillis = durationMillis;
    }

    public String getTitle() {
        if (title == null || " ".equals(title)) {
            String tmpTitle = new File(source).getName();
            tmpTitle = tmpTitle.replace("%20", " ");
            return tmpTitle;
        } else {
            return title;
        }
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public Media getMedia() {
        return new Media(source);
    }

    /**
     * Format the returning string to a nice form.
     * @return formatted String
     */
    @Override
    public String toString() {
        Duration duration = new Duration(durationMillis);
        int minutes = 0;
        int seconds = 0;
        String tmpTitle = getTitle();
        String tmpArtist = artist != null ? artist : "";
        String tmpAlbum = album != null ? album : "";
        if (duration != null) {
            minutes = (int) duration.toMinutes() % 60;
            seconds = (int) duration.toSeconds() % 60;
        }
        String niceFormat = String.format("%02d:%02d", minutes, seconds);
        return niceFormat + " | " + tmpTitle + " - " + tmpArtist + " [" + tmpAlbum + "] ";
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}
