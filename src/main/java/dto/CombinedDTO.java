package dto;

/** Our CombinedDTO class combines our three different Objects, that is returned 
 * as response from our Song resource endpoint. 
 * @author Daniel, Emil, Jannich, Jimmy
 */

public class CombinedDTO {
    private ITunesDTO itunes;
    private LyricsDTO lyrics;
    private SimilarDTO similar;

    public CombinedDTO(ITunesDTO itunes, LyricsDTO lyrics, SimilarDTO similar) {
        this.itunes = itunes;
        this.lyrics = lyrics;
        this.similar = similar;
    }
    
    public boolean isEmpty() {
        if(this.itunes.responseEqualsNull() && this.lyrics.responseEqualsNull()) {
            return true;
        } else {
            return false;
        }
    }
    
    
}
