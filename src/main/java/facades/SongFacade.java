
package facades;

import com.nimbusds.jose.JOSEException;
import dto.SongDTO;
import entities.Song;
import entities.User;
import errorhandling.API_Exception;
import errorhandling.Messages;
import java.text.ParseException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import security.JWTAuthenticationFilter;
import security.UserPrincipal;
import security.errorhandling.AuthenticationException;


public class SongFacade {
    
    private static EntityManagerFactory emf;
    private static SongFacade instance;

    private static final Messages MESSAGES = new Messages();

    private SongFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static SongFacade getSongFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new SongFacade();
        }
        return instance;
    }
    
    public SongDTO bookmarkSong(String songName, String artistName, int releaseYear, String albumName, String token) throws ParseException, JOSEException, API_Exception, AuthenticationException {
        JWTAuthenticationFilter jwtFilter = new JWTAuthenticationFilter();
        UserPrincipal userPrincipal = jwtFilter.getUserPrincipalFromTokenIfValid(token);
        
        EntityManager em = emf.createEntityManager();
        
        Song song = new Song(songName, artistName, releaseYear, albumName);
        if(song.isMissingSongName()) {
            throw new API_Exception(MESSAGES.cannotSaveSongMissingName, 424);
        } else {
            User user = em.find(User.class, userPrincipal.getName());
            user.addSong(song);
            song.addUser(user);

            em.getTransaction().begin();
                em.persist(song);
            em.getTransaction().commit();

            return new SongDTO(song);
        }
    }
    
}
