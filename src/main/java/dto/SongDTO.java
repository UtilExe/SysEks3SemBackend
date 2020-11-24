package dto;

public class SongDTO {
    private String song;
    private String artist;

    public SongDTO(String song, String artist) {
        this.song = song;
        this.artist = artist;
    }
    
    public String getSong() {
        return song;
    }

    public String getArtist() {
        return artist;
    } 
}
