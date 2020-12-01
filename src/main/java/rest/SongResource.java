package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nimbusds.jose.JOSEException;
import dto.CombinedDTO;
import dto.ITunesDTO;
import dto.LyricsDTO;
import dto.SimilarDTO;
import dto.SongDTO;
import entities.Song;
import entities.User;
import errorhandling.API_Exception;
import errorhandling.Messages;
import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import security.JWTAuthenticationFilter;
import security.UserPrincipal;
import security.errorhandling.AuthenticationException;
import utils.EMF_Creator;
import utils.Helper;
import utils.HttpUtils;
import utils.Keys;

@Path("song")
public class SongResource {
    
    private static final EntityManagerFactory emf = EMF_Creator.createEntityManagerFactory();
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String iTunesURL = "https://itunes.apple.com/search?country=DK&media=music&entity=song&limit=3&";
    private static final String lyricsURL = "https://api.lyrics.ovh/v1/";
    private static final String similarURL = "https://tastedive.com/api/similar";
    private static final ExecutorService es = Executors.newCachedThreadPool();
    private static Helper helper = new Helper();
    private static SecurityContext sc;
    
    private static final Messages messages = new Messages();
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String isUp() {
        return String.format("{\"message\":\"%s\"}", messages.serverIsUp);
    }
    
    @Path("search")
    @POST
    @RolesAllowed({"user", "admin"})
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String getSong(String song) throws InterruptedException, ExecutionException, TimeoutException, API_Exception {
        SongDTO track = gson.fromJson(song, SongDTO.class);
        track.setSong(helper.fixInput(track.getSong()));
        track.setArtist(helper.fixInput(track.getArtist()));
        return responseWithParallelFetch(es, track);
    }
    
    @Path("bookmark")
    @POST
    @RolesAllowed({"user", "admin"})
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String bookmarkSong(@HeaderParam("x-access-token") String token, String inputSong) throws InterruptedException, ExecutionException, TimeoutException, API_Exception, ParseException, JOSEException, AuthenticationException {
        JWTAuthenticationFilter jwtFilter = new JWTAuthenticationFilter();
        UserPrincipal userPrincipal = jwtFilter.getUserPrincipalFromTokenIfValid(token);
        SongDTO track = gson.fromJson(inputSong, SongDTO.class);
        EntityManager em = emf.createEntityManager();
        Song song = new Song(track.getSong(), track.getArtist(), track.getReleaseYear(), track.getAlbum());
        User user = em.find(User.class, userPrincipal.getName());
        user.addSong(song);
        song.addUser(user);
        em.getTransaction().begin();
        em.persist(song);
        em.getTransaction().commit();
        return gson.toJson(track);
    }

    public static String responseWithParallelFetch(ExecutorService threadPool, SongDTO track) throws InterruptedException, ExecutionException, TimeoutException, API_Exception {
        String song = track.getSong();
        String artist = track.getArtist();
        Callable<ITunesDTO> itunesTask = new Callable<ITunesDTO>() {
            @Override
            public ITunesDTO call() throws IOException {
                String itunes = HttpUtils.fetchData(iTunesURL + "term=" + song + "&limit=1");
                ITunesDTO iTunesDTO = gson.fromJson(itunes, ITunesDTO.class);
                return iTunesDTO;
            }
        };
        Callable<LyricsDTO> lyricTask = new Callable<LyricsDTO>() {
            @Override
            public LyricsDTO call() throws IOException {
                String lyric = HttpUtils.fetchData(lyricsURL + artist + "/" + song);
                LyricsDTO lyricsDTO = gson.fromJson(lyric, LyricsDTO.class);
                return lyricsDTO;
            }
        };
        Callable<SimilarDTO> similarTask = new Callable<SimilarDTO>() {
            @Override
            public SimilarDTO call() throws IOException {
                String similar = HttpUtils.fetchData(similarURL+ "?type=music&info=1&q="+ song + "&k=" + Keys.tastediveApi + "&limit=1");
                SimilarDTO similarDTO = gson.fromJson(similar, SimilarDTO.class);
                return similarDTO;
            }
        };

        Future<ITunesDTO> futureITunes = threadPool.submit(itunesTask);
        Future<LyricsDTO> futureLyrics = threadPool.submit(lyricTask);
        Future<SimilarDTO> futureSimilar = threadPool.submit(similarTask);

        ITunesDTO ITunes = futureITunes.get(3, TimeUnit.SECONDS);
        LyricsDTO lyrics = futureLyrics.get(3, TimeUnit.SECONDS);
        SimilarDTO similar = futureSimilar.get(3, TimeUnit.SECONDS);

        CombinedDTO combinedDTO = new CombinedDTO(ITunes, lyrics, similar);
        
        if(combinedDTO.isEmpty()) {
            throw new API_Exception(messages.songNotFound, 204);
        } else {
            String combinedJSON = gson.toJson(combinedDTO);

            return combinedJSON;
        }
        
    }
}
