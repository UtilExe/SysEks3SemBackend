package security;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.MalformedJsonException;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import dto.UserDTO;
import facades.UserFacade;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import entities.User;
import errorhandling.API_Exception;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import security.errorhandling.AuthenticationException;
import errorhandling.GenericExceptionMapper;
import javax.persistence.EntityManagerFactory;
import utils.EMF_Creator;
import errorhandling.Messages;
import javassist.tools.rmi.ObjectNotFoundException;
import javax.persistence.NoResultException;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;

@Path("admin")
public class AdminResource {

    public static final int TOKEN_EXPIRE_TIME = 1000 * 60 * 30; //30 min
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    public static final UserFacade USER_FACADE = UserFacade.getUserFacade(EMF);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final Messages MESSAGES = new Messages();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String isUp() {
        return String.format("{\"message\":\"%s\"}", MESSAGES.SERVER_IS_UP);
    }

    @POST
    @Path("delete")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response delete(String jsonString) throws AuthenticationException, API_Exception {
        String username;

        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            username = json.get("username").getAsString();
        } catch (Exception e) {
            throw new API_Exception(MESSAGES.MALFORMED_JSON, 400, e);
        }
        try {
            UserDTO user = USER_FACADE.deleteUser(username);
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("username", username);
            responseJson.addProperty("message", MESSAGES.DELETED_ACCOUNT_SUCCESS);
            return Response.ok(new Gson().toJson(responseJson)).build();

        } catch (Exception e) {
            if (e instanceof AuthenticationException) {
                throw new API_Exception(MESSAGES.USERNAME_ALREADY_EXISTS, 400, e);
            } else if (e instanceof UnsupportedOperationException) {
                throw new API_Exception(MESSAGES.PASSWORDS_DONT_MATCH, 400, e);
            } else if (e instanceof NoResultException) {
                throw new API_Exception(MESSAGES.USERNAME_DOESNT_EXIST, 400, e);
            } else {
                throw new API_Exception(MESSAGES.UNKNOWN_ERROR, 400, e);
            }
        }
    }
    
    @Path("all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllUsers() throws API_Exception {
        return gson.toJson(USER_FACADE.getAllUsers());
    }

    @Path("edit")
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response editUser(String jsonString) throws API_Exception {
        String username;
        String editedUsername;
        String editedPassword;
        try {
            JsonObject json = JsonParser.parseString(jsonString).getAsJsonObject();
            username = json.get("username").getAsString();
            //editedUsername = json.get("editedUsername").getAsString();
            editedPassword = json.get("editedPassword").getAsString();
        } catch (Exception e) {
            throw new API_Exception(MESSAGES.MALFORMED_JSON, 400, e);
        }
        try {
            UserDTO user = USER_FACADE.editUser(username, editedPassword);
            JsonObject responseJson = new JsonObject();
            responseJson.addProperty("username", username);
            responseJson.addProperty("message", MESSAGES.EDITUSER_SUCCES);
            return Response.ok(new Gson().toJson(responseJson)).build();

        } catch (NoResultException ex ) {
            throw new NoResultException(MESSAGES.USERNAME_DOESNT_EXIST);
        }
    }
    
}
