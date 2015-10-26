package de.victorfx.fxplayer.entity;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Ramon Victor on 26.10.2015.
 */
public class PlaylistEntity {

    private String name;
    private List<MediaEntity> mediaEntityList;
    private String pathToFile;

    public PlaylistEntity(String name, String pathToFile) {
        this.name = name;
        this.pathToFile = pathToFile;
        mediaEntityList = new LinkedList<>();
    }

    public String getPathToFile() {
        return pathToFile;
    }

    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<MediaEntity> getMediaEntityList() {
        return mediaEntityList;
    }

    public void addMediaEntity(MediaEntity mediaEntity) {
        mediaEntityList.add(mediaEntity);
    }

}
