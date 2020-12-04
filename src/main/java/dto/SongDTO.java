package dto;

import entities.Song;

public class SongDTO {
    private String song;
    private String artist;
    private int releaseYear;
    private String album;

    public SongDTO(Song song) {
        this.song = song.getName();
        this.artist = song.getArtist();
        this.releaseYear = song.getReleaseYear();
        this.album = song.getAlbum();
    }

    public String getSong() {
        return song;
    }

    public void setSong(String song) {
        this.song = song;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    @Override
    public String toString() {
        return "SongDTO{" + "song=" + song + ", artist=" + artist + ", releaseYear=" + releaseYear + ", album=" + album + '}';
    }
    
    
}
