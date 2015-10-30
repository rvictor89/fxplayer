package de.victorfx.fxplayer.entity;

import javafx.collections.ObservableList;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by Ramon Victor on 28.10.2015.
 */
@XmlRootElement(name = "Playlist")
public class PlaylistEntity {

    private ObservableList<MediaEntity> mediaEntities;

    public ObservableList<MediaEntity> getMediaEntities() {
        return mediaEntities;
    }

    public void setMediaEntities(ObservableList<MediaEntity> mediaEntities) {
        this.mediaEntities = mediaEntities;
    }

}
