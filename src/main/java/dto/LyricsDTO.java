package dto;

import java.util.Objects;

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

    public boolean responseEqualsNull() {
        if (this.lyrics.equals("")) {
            return true;
        }
        return false;
    }
    
    
    
    
}
