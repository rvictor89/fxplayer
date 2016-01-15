package de.victorfx.fxplayer.entity;

import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;

/**
 * Created by Ramon Victor on 15.01.2016.
 */
public class MediaEntityUnitTest {

    @Test
    public void testAlbum() {
        MediaEntity mediaEntity = BuildDummyMediaEntity();
        assertEquals("Album", mediaEntity.getAlbum());
    }

    @Test
    public void testToString() {
        String toString = "00:01 | Title - Artist [Album] ";
        MediaEntity mediaEntity = BuildDummyMediaEntity();
        assertEquals(toString, mediaEntity.toString());
    }

    @Test
    public void testArtist() {
        MediaEntity mediaEntity = BuildDummyMediaEntity();
        assertEquals("Artist", mediaEntity.getArtist());
    }

    @Test
    public void testDurationMillis() {
        MediaEntity mediaEntity = BuildDummyMediaEntity();
        assertEquals(1000d, mediaEntity.getDurationMillis());
    }

    @Test
    public void testSource() {
        MediaEntity mediaEntity = BuildDummyMediaEntity();
        assertEquals("Source", mediaEntity.getSource());
    }

    @Test
    public void testTitle() {
        MediaEntity mediaEntity = BuildDummyMediaEntity();
        assertEquals("Title", mediaEntity.getTitle());
    }

    @Test
    public void testAlternativeConstructor() {
        MediaEntity mediaEntity = new MediaEntity("Source");
        assertEquals("Source", mediaEntity.getSource());
    }

    @Test
    public void testMedia() throws IOException {
        File file = new File("Test.mp3");
        file.createNewFile();
        MediaEntity mediaEntity = new MediaEntity(new File("Test.mp3").toURI().toString());
        assertNotNull(mediaEntity.getMedia());
        file.deleteOnExit();
    }

    private MediaEntity BuildDummyMediaEntity() {
        MediaEntity mediaEntity = new MediaEntity();
        mediaEntity.setAlbum("Album");
        mediaEntity.setArtist("Artist");
        mediaEntity.setDurationMillis(1000);
        mediaEntity.setSource("Source");
        mediaEntity.setTitle("Title");
        return mediaEntity;
    }

}
