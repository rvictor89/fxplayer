package de.victorfx.fxplayer.entity;

import javafx.scene.media.Media;
import javafx.util.Duration;

/**
 * Created by Ramon Victor on 26.10.2015.
 */
public class MediaEntity {

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
        return title;
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

    @Override
    public String toString() {
        Duration duration = new Duration(durationMillis);
        int minutes = 0;
        int seconds = 0;
        if (duration != null) {
            minutes = (int) duration.toMinutes() % 60;
            seconds = (int) duration.toSeconds() % 60;
        }
        String niceFormat = String.format("%02d:%02d", minutes, seconds);
        return niceFormat + " | " + title + " - " + artist + " [" + album + "] ";
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

}
