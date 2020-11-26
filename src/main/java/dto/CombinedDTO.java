package dto;

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
        if(this.itunes == null && this.lyrics == null && this.similar == null) {
            return true;
        } else {
            return false;
        }
    }
    
    
}
