package com.mix.melody.CustomClass;





import android.media.MediaMetadataRetriever;

import com.mix.melody.ActivityUI.PlaySongActivity;
import com.mix.melody.UI.FavouriteFragment;


import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

public class Music  implements Serializable {
    private String id;
    private String title;
    private String album;
    private String artist;
    private long duration = 0;
    private String path;
    private String artUri;
    private ArrayList<Music> songList;

    public ArrayList<Music> getSongList() {
        return songList;
    }

    public void setSongList(ArrayList<Music> songList) {
        this.songList = songList;
    }
    public Music(long albumId, String albumTitle, String artistName, String albumArtUri, String artUriC) {
        this.album = String.valueOf(albumId);
        this.id = albumTitle;
        this.artist = artistName;
        this.artUri = (albumArtUri != null) ? albumArtUri.toString() : "";
    }


    public Music(String id, String title, String album, String artist, String path, long duration, String artUri) {
        this.id = id;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.path = path;
        this.duration = duration;
        this.artUri = artUri.toString();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public long getDuration() {
        return duration;
    }

    public String getPath() {
        return path;
    }

    public String getArtUri() {
        return artUri;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setPath(String path) {
        this.path = path;
    }



    public static byte[] getImgArt(String path) {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(path);
        return retriever.getEmbeddedPicture();
    }

    public static void SetPotionsOfSong(boolean increment) {
        if (!PlaySongActivity.repeat) {
            if (increment) {
                if (PlaySongActivity.musicListPA.size() == PlaySongActivity.songPosition + 1) {
                    PlaySongActivity.songPosition = 0;
                } else {
                    ++PlaySongActivity.songPosition;
                }
            } else {
                if (PlaySongActivity.songPosition == 0) {
                    PlaySongActivity.songPosition = PlaySongActivity.musicListPA.size() - 1;
                } else {
                    --PlaySongActivity.songPosition;
                }
            }
        }
    }

    public static int favouriteChecker(String id) {
        PlaySongActivity.isFavourite = false;

        for (int index = 0; index < FavouriteFragment.favouriteSongs.size(); index++) {
            Music music = FavouriteFragment.favouriteSongs.get(index);
            if (id.equals(music.getId())) {
                PlaySongActivity.isFavourite = true;
                return index;
            }
        }
        return -1;
    }


    public static ArrayList<Music> checkPlaylist(ArrayList<Music> playlist) {
        for (int i = 0; i < playlist.size(); i++) {
            Music music = playlist.get(i);
            File file = new File(music.getPath());
            if (!file.exists()) {
                playlist.remove(i);
                i--;
            }
        }
        return playlist;
    }
    public static class Playlist {
        public String name;
        public ArrayList<Music> playlist;
        public String createdBy;
        public String createdOn;
    }

    public static class MusicPlaylist {
        public ArrayList<Playlist> ref = new ArrayList<>();
    }

}