package dto;

/** SimilarDTO is used to return two parameters, from our Similar endpoint/API. 
 * @author Daniel, Emil, Jannich, Jimmy
 */

public class SimilarDTO {
    // Capitalized by choice! No mistake.
    private Object Similar;
    private String search;

    public String getSearch() {
        return search;
    }
}
