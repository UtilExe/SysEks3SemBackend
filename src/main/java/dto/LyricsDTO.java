package dto;

public class LyricsDTO {
    
    String artistName;
    String songName;
    Object lyrics;

    public LyricsDTO(String artistName, String songName) {
        this.artistName = artistName;
        this.songName = songName;
    }
    
    

    public String getArtistName() {
        return artistName;
    }

    public String getSongName() {
        return songName;
    }
    
    

    public Object getLyrics() {
        return lyrics;
    }
    
    
}
