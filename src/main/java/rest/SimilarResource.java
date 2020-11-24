package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.SimilarDTO;
import facades.FacadeExample;
import java.io.IOException;
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

@Path("similar")
public class SimilarResource {
    
    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory();
    private static final FacadeExample facade =  FacadeExample.getFacadeExample(EMF);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static Helper helper = new Helper();
    
    private static final String URL = "https://tastedive.com/api/similar";
    
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }
    
    @Path("search")
    @POST
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String getSimilar(String search) throws IOException {
        SimilarDTO similarDTO = gson.fromJson(search, SimilarDTO.class);
        String similar = HttpUtils.fetchData(URL+ "?type=music&info=1&q="+ similarDTO.getSearch() + "?k=" + Keys.tastediveApi);
        similarDTO = gson.fromJson(similar, SimilarDTO.class);
        
        return gson.toJson(similarDTO);
    }
    
}
