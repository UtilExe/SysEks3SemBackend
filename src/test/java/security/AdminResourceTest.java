
package security;

import entities.Role;
import entities.User;
import errorhandling.Messages;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import rest.ApplicationConfig;
import utils.EMF_Creator;


public class AdminResourceTest {
    
     private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    
    private static final Messages MESSAGES = new Messages();
    
    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactoryForTest();

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;
    }

    @AfterAll
    public static void closeTestServer() {
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            //Delete existing users and roles to get a "fresh" database
            em.createQuery("delete from User").executeUpdate();
            em.createQuery("delete from Role").executeUpdate();

            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            User user = new User("user", "test");
            user.addRole(userRole);
            User admin = new User("admin", "test");
            admin.addRole(adminRole);
            User both = new User("user_admin", "test");
            both.addRole(userRole);
            both.addRole(adminRole);
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.persist(both);
            //System.out.println("Saved test data to database");
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    //This is how we hold on to the token after login, similar to that a client must store the token somewhere
    private static String securityToken;

    //Utility method to login and set the returned securityToken
    private static void login(String role, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", role, password);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                .when().post("/login")
                .then()
                .extract().path("token");
    }

    private void logOut() {
        securityToken = null;
    }
    
    public AdminResourceTest() {
    }
    
    @Test
    public void serverIsRunningTest() {
        given().when().get("/admin").then().statusCode(200);
    }

    @Test
    public void getAllUsersTest() {
        login("admin", "test");
        
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when().get("/admin/all").then()
                .statusCode(200);
    }
    
    @Test
    public void deleteUserTest() {
        String username = "user";
        
        String jsonRequest = String.format(
                "{\"username\": \"%s\"}", username);
        
        login("admin", "test");
        
        given()
                .contentType("application/json")
                .body(jsonRequest)
                .header("x-access-token", securityToken)
                .when().post("/admin/delete").then()
                .statusCode(200)
                .body("username", equalTo(username))
                .body("message", equalTo(MESSAGES.DELETED_ACCOUNT_SUCCESS));
    }
    
    @Test
    public void editUserTest() {
        String username = "user";
        String password = "hablahabla";
        
        String jsonRequest = String.format(
                "{\"username\": \"%s\","
                + "\"editedPassword\": \"%s\"}", username, password);
        
        login("admin", "test");
        
        given()
                .contentType("application/json")
                .body(jsonRequest)
                .header("x-access-token", securityToken)
                .when().put("/admin/edit").then()
                .statusCode(200)
                .body("username", equalTo(username))
                .body("message", equalTo(MESSAGES.EDITUSER_SUCCES));
    }
    
    
    
}
