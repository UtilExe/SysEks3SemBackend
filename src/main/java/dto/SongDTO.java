package dto;

public class SongDTO {
    private String song;
    private String artist;
    private int releaseYear;
    private String album;

    public SongDTO(String song, String artist, int releaseYear, String album) {
        this.song = song;
        this.artist = artist;
        this.releaseYear = releaseYear;
        this.album = album;
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
}
