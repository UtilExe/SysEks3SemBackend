package dto;

import java.util.Objects;

public class SimilarDTO {
    // Capitalized by choice! No mistake.
    private Object Similar;
    private String search;

    public String getSearch() {
        return search;
    }
    
    public boolean responseEqualsNull() {
        if (this.Similar == null) {
            return true;
        }
        return false;
    }
}
