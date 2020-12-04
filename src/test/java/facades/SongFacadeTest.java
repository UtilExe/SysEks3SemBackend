
package facades;

import dto.SongDTO;
import entities.Role;
import entities.Song;
import entities.User;
import errorhandling.API_Exception;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import utils.EMF_Creator;


public class SongFacadeTest {
    
    private static EntityManagerFactory emf;
    private static SongFacade facade;
    
    private static User user = new User("user", "password");

    /*
    String name, String artist, int releaseYear, String album
     */
    public SongFacadeTest() {
    }

    @BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactoryForTest();
        facade = SongFacade.getSongFacade(emf);
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
    public void bookmarkSongTest() throws API_Exception {
        String songName = "Levels";
        String artistName = "Avicii";
        int releaseYear = 2011;
        String albumName = "";
        
        User expectedUser = user;
        Song expectedSong = new Song(songName, artistName, releaseYear, albumName);
        expectedUser.addSong(expectedSong);
        
        SongDTO expected = new SongDTO(expectedSong);
        
        SongDTO result = facade.bookmarkSong(songName, artistName, releaseYear, albumName, user.getUserName());
        
        assertEquals(expected.getSong(), result.getSong());
        assertEquals(expected.getArtist(), result.getArtist());
    }
    
    
}
