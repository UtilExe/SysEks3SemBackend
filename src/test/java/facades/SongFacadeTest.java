
package facades;

import dto.SongDTO;
import entities.Role;
import entities.Song;
import entities.User;
import errorhandling.API_Exception;
import java.util.ArrayList;
import java.util.List;
import javassist.tools.rmi.ObjectNotFoundException;
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
    
    private static Song song1 = new Song("Levels", "Avicii", 2011, "");
    private static Song song2 = new Song("Intro", "M83", 2011, "");
    private static Song song3 = new Song("Silhouettes", "Avicii", 2011, "");
            
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
            
            Song song1 = new Song("Levels", "Avicii", 2011, "");
            Song song2 = new Song("Intro", "M83", 2011, "");
            Song song3 = new Song("Silhouettes", "Avicii", 2011, "");
            
            User user = new User("user", "password");

            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            user.addRole(userRole);
            User admin = new User("admin", "password");
            admin.addRole(adminRole);
            User both = new User("user_admin", "password");
            both.addRole(userRole);
            both.addRole(adminRole);
            
            user.addSong(song1);
            song1.addUser(user);
            user.addSong(song2);
            song2.addUser(user);
            user.addSong(song3);
            song3.addUser(user);
            
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(admin);
            em.persist(both);
            em.persist(user);
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
    
    @Test
    public void showSavedSongsTest() throws API_Exception, ObjectNotFoundException {
        Song song1 = new Song("Levels", "Avicii", 2011, "");
        Song song2 = new Song("Intro", "M83", 2011, "");
        Song song3 = new Song("Silhouettes", "Avicii", 2011, "");
        
        List<SongDTO> expected = new ArrayList();
        expected.add(new SongDTO(song1));
        expected.add(new SongDTO(song2));
        expected.add(new SongDTO(song3));
        
        List<SongDTO> result = facade.showSavedSongs(user.getUserName());
        
        assertTrue(result.toString().contains(expected.get(0).toString()));
        assertTrue(result.toString().contains(expected.get(1).toString()));
        assertTrue(result.toString().contains(expected.get(2).toString()));
    }
    
    
}
