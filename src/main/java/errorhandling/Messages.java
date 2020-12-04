
package errorhandling;


public class Messages {
    
    public final String MALFORMED_JSON = "Malformed JSON Suplied";
    public final String ACCOUNT_CREATED = "Your account has been created";
    public final String USERNAME_ALREADY_EXISTS = "Username already exists";
    public final String PASSWORDS_DONT_MATCH = "Passwords doesn't match";
    public final String INVALID_USERNAME_OR_PWD = "Invalid username or password! Please try again";
    public final String UNKNOWN_ERROR = "Something went wrong";
    public final String NOT_AUTHORIZED = "You are not authorized to perform the requested operation";
    public final String RESOURCE_NOT_FOUND = "Resource Not Found";
    public final String NOT_AUTHENTICADED = "Not authenticated - do login";
    public final String TOKEN_INVALID_OR_EXPIRED = "Token not valid (timed out?)";
    public final String TOKEN_EXPIRED = "Your Token is no longer valid";
    public final String TOKEN_CANNOT_EXTRACT_USER = "User could not be extracted from token";
    public final String SONG_NOT_FOUND = "We couldn’t find information about that song...";
    public final String SERVER_IS_UP = "Server is up";
    public final String USERNAME_DOESNT_EXIST = "The username doesn't exist! Can't delete user";
    public final String DELETED_ACCOUNT_SUCCESS = "The user has been deleted";
    public final String CANNOT_SAVE_SONG_MISSING_NAME = "We couldn't save the song - song name missing";
    public final String NO_USERS_FOUND = "No users found";
    public final String NO_SONGS_FOUND = "No songs found";
    public final String EDITUSER_SUCCES = "The user has been updated!";
    public Messages() {
    }
    
}
