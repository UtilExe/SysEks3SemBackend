package utils;

/** The Keys class stores our secret key(s), that the external API's use. 
 * It's specified as a "getenv" that we specify on our Droplet in our docker-compose.yml file.
 * @author Daniel, Emil, Jannich, Jimmy
 */

public class Keys {
    public static String tastediveApi = System.getenv("tastediveApi");

}