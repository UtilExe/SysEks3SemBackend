package utils;

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
            input = input.replaceAll(" ", "%20");
        }
            
        return input;
    }
}
