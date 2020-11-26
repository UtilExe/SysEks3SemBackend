
package dto;

import java.util.ArrayList;
import java.util.Objects;

public class ITunesDTO {
    
    Object results;
    String trackName;
    String trackPrice;

    public Object getResults() {
        return results;
    }

    public String getTrackName() {
        return trackName;
    }

    public String getTrackPrice() {
        return trackPrice;
    }
    
    public boolean responseEqualsNull() {
        if (this.results.equals(new ArrayList())) {
            return true;
        }
        return false;
    }
    
    
}
