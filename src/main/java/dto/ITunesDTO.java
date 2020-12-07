
package dto;

import java.util.ArrayList;

/**
 * @author Daniel, Emil, Jannich, Jimmy
 * ITunesDTO returns the response from our ITunes endpoint/API.
 */

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
        if (this.results.equals(new ArrayList<String>())) {
            return true;
        }
        return false;
    }
    
    
}
