package de.victorfx.fxplayer.entity;

/**
 * Created by Ramon Victor on 26.10.2015.
 */
public class MediaEntity {

    private String title;
    private String artist;
    private String album;
    private String pathToFile;
    private double totalDurationInSeconds;
    private double totalDurationInMinutes;

    public MediaEntity(String pathToFile) {
        this.pathToFile = pathToFile;
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

    public String getPathToFile() {
        return pathToFile;
    }

    public void setPathToFile(String pathToFile) {
        this.pathToFile = pathToFile;
    }

    public double getTotalDurationInSeconds() {
        return totalDurationInSeconds;
    }

    public void setTotalDurationInSeconds(double totalDurationInSeconds) {
        this.totalDurationInSeconds = totalDurationInSeconds;
    }

    public double getTotalDurationInMinutes() {
        return totalDurationInMinutes;
    }

    public void setTotalDurationInMinutes(double totalDurationInMinutes) {
        this.totalDurationInMinutes = totalDurationInMinutes;
    }

}
