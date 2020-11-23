package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.nimbusds.jose.shaded.json.JSONObject;
import dto.ITunesDTO;
import facades.FacadeExample;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.annotation.security.RolesAllowed;
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
import utils.Keys;

@Path("itunes")
public class ITunesResource {
    
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
       
    private static final FacadeExample facade =  FacadeExample.getFacadeExample(EMF);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final String iTunesURL = "https://itunes.apple.com/search?country=DK&media=music&entity=song&limit=3&";
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

    // TODO: Return only trackPrice, so we don't have to do it on frontend on website + app.
    public String getPriceBySong(String trackName) throws IOException {
        ITunesDTO iTunesDTO = gson.fromJson(trackName, ITunesDTO.class);
        String itunes = HttpUtils.fetchData(iTunesURL + "term=" + iTunesDTO.getTrackName());
        iTunesDTO = gson.fromJson(itunes, ITunesDTO.class);
        
        return gson.toJson(iTunesDTO);
    }
}
