package utils;

/**
 * @author Daniel, Emil, Jannich, Jimmy
 * The Helper class is used in our SongResource, to handle "æøå" and spaces.
 * It's needed because several of our external API's don't support spacing and special symbols,
 * So through our Helper class, we convert it to what is supported, in a generic way.
 */

public class Helper {

    public String fixInput(String input) {
        input = input.toLowerCase();
        
        if(input.contains("å"))
        {
            input = input.replaceAll("å", "aa");
        } 
        
        if(input.contains("ø"))
        {
            input = input.replaceAll("ø", "oe");
        }
        
        if(input.contains("æ"))
        {
            input = input.replaceAll("æ", "ae");
        }
        
        if(input.contains(" "))
        {
            input = input.replaceAll(" ", "+");
        }
            
        return input;
    }
}
