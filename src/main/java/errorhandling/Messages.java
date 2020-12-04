
package errorhandling;


public class Messages {
    
    public final String malformedJson = "Malformed JSON Suplied";
    public final String accountCreated = "Your account has been created";
    public final String usernameAlreadyExists = "Username already exists";
    public final String passwordsNotMatch = "Passwords doesn't match";
    public final String invalidUsernameOrPwd = "Invalid username or password! Please try again";
    public final String unknownError = "Something went wrong";
    public final String notAuthorized = "You are not authorized to perform the requested operation";
    public final String resourceNotFound = "Resource Not Found";
    public final String notAuthenticaded = "Not authenticated - do login";
    public final String tokenInvalidOrExpired = "Token not valid (timed out?)";
    public final String tokenExpired = "Your Token is no longer valid";
    public final String tokenCannotExtractUser = "User could not be extracted from token";
    public final String songNotFound = "We couldn’t find information about that song...";
    public final String serverIsUp = "Server is up";
    public final String usernameDoesntExist = "The username doesn't exist! Can't delete user";
    public final String deletedAccount = "The user has been deleted";
    public final String cannotSaveSong = "We couldn't save that song as no information about it was found";

    public Messages() {
    }
    
}
