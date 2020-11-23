package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ITunesDTO;
import dto.LyricsDTO;
import facades.FacadeExample;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import utils.EMF_Creator;
import utils.Helper;
import utils.HttpUtils;

@Path("lyrics")
public class LyricsovhResource {
    
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
       
    private static final FacadeExample facade =  FacadeExample.getFacadeExample(EMF);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String lyricsURL = "https://api.lyrics.ovh/v1/";
    private static final ExecutorService es = Executors.newCachedThreadPool();
    private static Helper helper = new Helper();
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }
    
    @Path("search")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})

    public String getLyricsBySong(String lyrics) throws IOException {
        LyricsDTO lyricsDTO = gson.fromJson(lyrics, LyricsDTO.class);
        String test = HttpUtils.fetchData(lyricsURL + lyricsDTO.getArtistName() + "/" + lyricsDTO.getSongName());
        lyricsDTO = gson.fromJson(test, LyricsDTO.class);
        
        return gson.toJson(lyricsDTO);
    }
}
