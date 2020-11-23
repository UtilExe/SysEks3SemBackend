
package facades;

import entities.RenameMe;
import entities.Role;
import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;


public class UserFacadeTest 
{
    private static EntityManagerFactory emf;
    private static UserFacade facade;
    
    private static User user = new User("user", "password");
    private static User admin = new User("admin", "password");
    private static User both = new User("user_admin", "password");
    
    public UserFacadeTest() {}
    
    @BeforeAll
    public static void setUpClass() {
       emf = EMF_Creator.createEntityManagerFactoryForTest();
       facade = UserFacade.getUserFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() 
    {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the code below to use YOUR OWN entity class
    @BeforeEach
    public void setUp() 
    {
        EntityManager em = emf.createEntityManager();
        try 
        {
            em.getTransaction().begin();
                em.createNamedQuery("Role.deleteAllRows").executeUpdate();
                em.createNamedQuery("User.deleteAllRows").executeUpdate();
                Role userRole = new Role("user");
                Role adminRole = new Role("admin");
                user.addRole(userRole);
                admin.addRole(adminRole);
                both.addRole(userRole);
                both.addRole(adminRole);
                em.persist(userRole);
                em.persist(adminRole);
                em.persist(user);
                em.persist(admin);
                em.persist(both);
            em.getTransaction().commit();
        } finally 
        {
            em.close();
        }
    }
    
    @Test
    public void getVeryfiedUserTest() throws AuthenticationException 
    {
        User expected = user;
        User result = facade.getVeryfiedUser(user.getUserName(), "password");
        
        assertEquals(expected.getUserPass(), result.getUserPass());
    }
}
