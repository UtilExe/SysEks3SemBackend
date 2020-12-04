package facades;

import dto.UserDTO;
import entities.Role;
import entities.Song;
import entities.User;
import errorhandling.API_Exception;
import java.util.ArrayList;
import java.util.List;
import javassist.tools.rmi.ObjectNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;

public class UserFacadeOtherTest {

    private static EntityManagerFactory emf;
    private static UserFacade facade;

    /*
    String name, String artist, int releaseYear, String album
     */
    public UserFacadeOtherTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = UserFacade.getUserFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the code below to use YOUR OWN entity class
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
            User user = new User("user", "password");
            user.addRole(userRole);
            User admin = new User("admin", "password");
            admin.addRole(adminRole);
            User both = new User("user_admin", "password");
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

    @Test
    public void createAccountTesting() throws AuthenticationException {

        String username = "Hans";
        String password = "Yes123";
        User userAccTest = new User(username, password);
        UserDTO realAcc = facade.createUser(username, password, password);

        assertEquals(userAccTest.getUserName(), realAcc.getUsername());
    }
    
    @Test
    public void getAllUsersTest() throws ObjectNotFoundException, API_Exception {
        List<UserDTO> userList = facade.getAllUsers();
        
        assertThat(userList, hasSize(3));
    }
    
    @Test
    public void deleteUserTest() throws AuthenticationException, API_Exception, ObjectNotFoundException {
        facade.deleteUser("user");

        List<UserDTO> userList = facade.getAllUsers();
        assertThat(userList, hasSize(2));
    }
    
    @Test
    public void editUserTest() throws AuthenticationException, API_Exception, ObjectNotFoundException {
        EntityManager em = emf.createEntityManager();  
        
        User userBefore = em.find(User.class, "user");
        String beforePassword = userBefore.getUserPass();
        
        /*Use clear to detach all managed entities. 
        In other words, find will have the same password if "em.clear()" is not used.*/
        em.clear();

        UserDTO habla = facade.editUser("user", "kebablalala");
        
        User userAfter = em.find(User.class, "user");
        String afterPassword = userAfter.getUserPass();
        
        em.close();

        if(!beforePassword.equals(afterPassword)) {
            assertTrue(true);
        } else {
            assertTrue(false);
        }
    }

}
