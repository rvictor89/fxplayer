package de.victorfx.fxplayer.entity;

import de.victorfx.fxplayer.model.MediaEntity;
import de.victorfx.fxplayer.model.PlaylistEntity;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertEquals;

/**
 * Created by Ramon Victor on 15.01.2016.
 */
public class PlaylistEntityUnitTest {

    @Test
    public void testPlaylist() {
        List<MediaEntity> mediaEntities = new ArrayList<>();
        MediaEntity mediaEntity1 = new MediaEntity();
        MediaEntity mediaEntity2 = new MediaEntity();
        mediaEntities.add(mediaEntity1);
        mediaEntities.add(mediaEntity2);

        PlaylistEntity playlistEntity = new PlaylistEntity();
        playlistEntity.setMediaEntityList(mediaEntities);
        assertEquals(mediaEntities, playlistEntity.getMediaEntityList());
    }

}
