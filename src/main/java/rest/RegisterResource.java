package rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dto.UserDTO;
import facades.UserFacade;
import errorhandling.API_Exception;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import security.errorhandling.AuthenticationException;
import javax.persistence.EntityManagerFactory;
import utils.EMF_Creator;
import errorhandling.Messages;
import javax.ws.rs.GET;

/** RegisterResource is a Rest class with an endpoint, that supports creating an account. 
 * This includes an username, password, and passwordCheck as fields, and is then a POST.
 * It makes use of our UserDTO, and UserFacade.
 * @author Daniel, Emil, Jannich, Jimmy
 */

@Path("register")
public class RegisterResource {

    public static final int TOKEN_EXPIRE_TIME = 1000 * 60 * 30; //30 min
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    public static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);
    
    private static final Messages messages = new Messages();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String isUp() {
        return String.format("{\"message\":\"%s\"}", messages.SERVER_IS_UP);
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response register(String jsonString) throws AuthenticationException, API_Exception {
        String username;
        String password;
        String passwordCheck;
        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            username = json.get("username").getAsString();
            password = json.get("password").getAsString();
            passwordCheck = json.get("passwordCheck").getAsString();
        } catch (Exception e) {
            throw new API_Exception(messages.MALFORMED_JSON, 400, e);
        }

        try {
            UserDTO user = USER_FACADE.createUser(username, password, passwordCheck);
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("username", username);
          /*  responseJson.addProperty("password", password);
            responseJson.addProperty("passwordCheck", passwordCheck);*/
            responseJson.addProperty("message", messages.ACCOUNT_CREATED);
            return Response.ok(new Gson().toJson(responseJson)).build();

        } catch (Exception e) {
            if (e instanceof AuthenticationException) {
                throw new API_Exception(messages.USERNAME_ALREADY_EXISTS, 400, e);
            } else if(e instanceof UnsupportedOperationException) {
                throw new API_Exception(messages.PASSWORDS_DONT_MATCH, 400, e);
            } else {
                throw new API_Exception(messages.UNKNOWN_ERROR, 400, e);
            }
        }
    }
}
