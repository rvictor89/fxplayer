package de.victorfx.fxplayer.entity;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by Ramon Victor on 28.10.2015.
 */
@XmlRootElement(name = "Playlist")
public class PlaylistEntity {

    private List<MediaEntity> mediaEntityList;

    public List<MediaEntity> getMediaEntityList() {
        return mediaEntityList;
    }

    public void setMediaEntityList(List<MediaEntity> mediaEntityList) {
        this.mediaEntityList = mediaEntityList;
    }

}
