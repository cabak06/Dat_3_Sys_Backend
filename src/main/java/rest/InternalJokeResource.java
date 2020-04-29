package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.InternalJokeDTO;
import dto.InternalJokesDTO;
import utils.EMF_Creator;
import facades.InternalJokeFacade;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

//Todo Remove or change relevant parts before ACTUAL use
@Path("joke")
public class InternalJokeResource {
    
    @Context
    SecurityContext securityContext;

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(
                "pu",
                "jdbc:mysql://localhost:3307/SysEksamenJokes",
                "dev",
                "ax2",
                EMF_Creator.Strategy.CREATE);
    private static final InternalJokeFacade FACADE =  InternalJokeFacade.getFacadeExample(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
            
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }
    
    
    @GET
    @Path("count")
    @Produces({MediaType.APPLICATION_JSON})
    public String getRenameMeCount() {
        long count = FACADE.getInternalJokeCount();
        return "{\"count\":"+count+"}";  //Done manually so no need for a DTO
    }
    
    @POST
    @RolesAllowed("user")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String addJoke(String joke) {
        InternalJokeDTO jokeAdd = GSON.fromJson(joke, InternalJokeDTO.class);
        String thisuser = securityContext.getUserPrincipal().getName();
        jokeAdd.setCreatedBy(thisuser);
        
        jokeAdd = FACADE.addJoke(jokeAdd);
        return GSON.toJson(jokeAdd);
    }
    
    @Path("userjokes")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String getUserJokes() {
    InternalJokesDTO allJokes = FACADE.getUserJokes();
    return GSON.toJson(allJokes);
    }
    
    @Path("delete")
    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String deleteUserJokes() {
    InternalJokeDTO deleteJoke = FACADE.deleteUserJoke();
    return GSON.toJson(deleteJoke);
    }
}