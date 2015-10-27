package de.victorfx.fxplayer.entity;

import javafx.scene.media.Media;
import javafx.util.Duration;

/**
 * Created by Ramon Victor on 26.10.2015.
 */
public class MediaEntity {

    private Media media;
    private String title;
    private String artist;
    private String album;

    public MediaEntity(Media media) {
        this.media = media;
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
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
    }

    @Override
    public String toString() {
        Duration duration = media.getDuration();
        int  minutes = (int) duration.toMinutes() % 60;
        int seconds = (int) duration.toSeconds() % 60;
        String niceFormat = String.format("%02d:%02d", minutes, seconds);
        return niceFormat + " | " + title + " - " + artist + " [" + album + "] ";
    }
}
