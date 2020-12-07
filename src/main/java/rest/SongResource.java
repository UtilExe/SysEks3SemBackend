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
import facades.SongFacade;
import java.io.IOException;
import java.text.ParseException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import javassist.tools.rmi.ObjectNotFoundException;
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

/**
 * @author Daniel, Emil, Jannich, Jimmy
 * The SongResource is one our Rest functionality classes, where you can reach 
 * our Endpoints through a Path, and a Method call (POST/GET etc.)
 * It uses our DTO classes, our Facades, and specifies our external API's.
 */

@Path("song")
public class SongResource {
    
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String ITUNES_URL = "https://itunes.apple.com/search?country=DK&media=music&entity=song&limit=3&";
    private static final String LYRICS_URL = "https://api.lyrics.ovh/v1/";
    private static final String SIMILAR_URL = "https://tastedive.com/api/similar";
    private static final ExecutorService ES = Executors.newCachedThreadPool();
    private static Helper helper = new Helper();
    private static SecurityContext sc;
    
    public static final SongFacade SONG_FACADE = SongFacade.getSongFacade(EMF);
    
    private static final Messages MESSAGES = new Messages();
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String isUp() {
        return String.format("{\"message\":\"%s\"}", MESSAGES.SERVER_IS_UP);
    }
    
    @Path("search")
    @POST
    @RolesAllowed({"user", "admin"})
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String getSong(String song) throws InterruptedException, ExecutionException, TimeoutException, API_Exception {
        SongDTO track = GSON.fromJson(song, SongDTO.class);
        track.setSong(helper.fixInput(track.getSong()));
        track.setArtist(helper.fixInput(track.getArtist()));
        return responseWithParallelFetch(ES, track);
    }
    
    @Path("bookmark")
    @POST
    @RolesAllowed({"user", "admin"})
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String bookmarkSong(@HeaderParam("x-access-token") String token, String inputSong) throws InterruptedException, ExecutionException, TimeoutException, API_Exception, ParseException, JOSEException, AuthenticationException {
        JWTAuthenticationFilter jwtFilter = new JWTAuthenticationFilter();
        UserPrincipal userPrincipal = jwtFilter.getUserPrincipalFromTokenIfValid(token);
        String username = userPrincipal.getName();
        
        SongDTO track = GSON.fromJson(inputSong, SongDTO.class);
        SONG_FACADE.bookmarkSong(track.getSong(), track.getArtist(), track.getReleaseYear(), track.getAlbum(), username);
        
        return GSON.toJson(track);
    }
    
    @Path("user/all")
    @GET
    @RolesAllowed({"user", "admin"})
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String showSavedSongs(@HeaderParam("x-access-token") String token) throws InterruptedException, ExecutionException, TimeoutException, API_Exception, ParseException, JOSEException, AuthenticationException, ObjectNotFoundException {
        JWTAuthenticationFilter jwtFilter = new JWTAuthenticationFilter();
        UserPrincipal userPrincipal = jwtFilter.getUserPrincipalFromTokenIfValid(token);
        String username = userPrincipal.getName();
        
        return GSON.toJson(SONG_FACADE.showSavedSongs(username));
    }

    public static String responseWithParallelFetch(ExecutorService threadPool, SongDTO track) throws InterruptedException, ExecutionException, TimeoutException, API_Exception {
        String song = track.getSong();
        String artist = track.getArtist();
        Callable<ITunesDTO> itunesTask = new Callable<ITunesDTO>() {
            @Override
            public ITunesDTO call() throws IOException {
                String itunes = HttpUtils.fetchData(ITUNES_URL + "term=" + song + "&limit=1");
                ITunesDTO iTunesDTO = GSON.fromJson(itunes, ITunesDTO.class);
                return iTunesDTO;
            }
        };
        Callable<LyricsDTO> lyricTask = new Callable<LyricsDTO>() {
            @Override
            public LyricsDTO call() throws IOException {
                String lyric = HttpUtils.fetchData(LYRICS_URL + artist + "/" + song);
                LyricsDTO lyricsDTO = GSON.fromJson(lyric, LyricsDTO.class);
                return lyricsDTO;
            }
        };
        Callable<SimilarDTO> similarTask = new Callable<SimilarDTO>() {
            @Override
            public SimilarDTO call() throws IOException {
                String similar = HttpUtils.fetchData(SIMILAR_URL+ "?type=music&info=1&q="+ song + "&k=" + Keys.tastediveApi + "&limit=1");
                SimilarDTO similarDTO = GSON.fromJson(similar, SimilarDTO.class);
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
            throw new API_Exception(MESSAGES.SONG_NOT_FOUND, 404);
        } else {
            String combinedJSON = GSON.toJson(combinedDTO);

            return combinedJSON;
        }
        
    }
}
