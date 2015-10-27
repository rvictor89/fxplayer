package de.victorfx.fxplayer.entity;

import javafx.scene.media.Media;

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

    public Media getMedia() {
        return media;
    }

    public void setMedia(Media media) {
        this.media = media;
        title = (String) media.getMetadata().get("title");
        artist = (String) media.getMetadata().get("artist");
        album = (String) media.getMetadata().get("album");
    }

    @Override
    public String toString() {
        return "MediaEntity{" +
                "media=" + media +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                ", album='" + album + '\'' +
                '}';
    }
}
