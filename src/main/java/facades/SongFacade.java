
package facades;

import com.nimbusds.jose.JOSEException;
import dto.SongDTO;
import entities.Song;
import entities.User;
import errorhandling.API_Exception;
import errorhandling.Messages;
import java.text.ParseException;
import javassist.tools.rmi.ObjectNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
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
    
    public SongDTO bookmarkSong(String songName, String artistName, int releaseYear, String albumName, String username) throws API_Exception
    {
        EntityManager em = emf.createEntityManager();
        
        Song song = new Song(songName, artistName, releaseYear, albumName);
        if(song.isMissingSongName()) {
            throw new API_Exception(MESSAGES.CANNOT_SAVE_SONG_MISSING_NAME, 424);
        } else {
            User user = em.find(User.class, username);
            user.addSong(song);
            song.addUser(user);

            em.getTransaction().begin();
                em.persist(song);
            em.getTransaction().commit();

            return new SongDTO(song);
        }
    }
    
    public List<SongDTO> showSavedSongs(String username) throws API_Exception {
        EntityManager em = emf.createEntityManager();
        List<Song> allSongs = new ArrayList();
        List<SongDTO> allSongsDTO = new ArrayList();

        try {
            TypedQuery<Song> query = em.createQuery("SELECT s FROM Song s, User u WHERE u.userName = :username", Song.class)
                    .setParameter("username", username);
            allSongs = query.getResultList();
            
            if (allSongs.isEmpty() || allSongs == null) {
                throw new API_Exception(MESSAGES.NO_SONGS_FOUND, 404);
            }

            for (Song song : allSongs) {
                allSongsDTO.add(new SongDTO(song));
            }

            return allSongsDTO;
        } finally {
            em.close();
        }
    }
    
    
}
