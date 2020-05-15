package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.ChuckJokeDTO;
import dto.DadJokeDTO;
import dto.ApiDTO;
import dto.ExternalJokeDTO;
import dto.ExternalJokesDTO;
import dto.JokerDTO;
import entities.User;
import facades.ExternalJokeFacade;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import utils.EMF_Creator;
import utils.HttpUtils;

@Path("externalJoke")
public class ExternalJokeResource {

    private static EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);
    @Context
    SecurityContext securityContext;
 
    private static final ExternalJokeFacade FACADE = ExternalJokeFacade.getJokeFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("random")
    public String getFromExternalAPI() {

        String[] fetchStrings = new String[]{
            ChuckJokeDTO.getRANDOM_URL(),
            DadJokeDTO.getRANDOM_URL(),
            JokerDTO.getRANDOM_URL()
        };
        String[] fetched = new String[fetchStrings.length];
        ExecutorService workingJack = Executors.newFixedThreadPool(fetchStrings.length);

        try {
            for (int i = 0; i < fetchStrings.length; i++) {
                final int n = i;
                Runnable task = () -> {
                    try {
                        fetched[n] = HttpUtils.fetchData(fetchStrings[n]);
                    } catch (IOException ex) {
                        Logger.getLogger(ExternalJokeResource.class.getName()).log(Level.SEVERE, null, ex);
                    }
                };
                workingJack.submit(task);
            }

            workingJack.shutdown();
            workingJack.awaitTermination(5, TimeUnit.SECONDS);

            ChuckJokeDTO chuck = GSON.fromJson(fetched[0], ChuckJokeDTO.class);
            DadJokeDTO dad = GSON.fromJson(fetched[1], DadJokeDTO.class);
            JokerDTO joker = GSON.fromJson(fetched[2], JokerDTO.class);
            joker.fixJoke();
            
            ApiDTO apis = new ApiDTO(chuck, dad, joker);
            return GSON.toJson(apis);
        } catch (InterruptedException ex) {
            Logger.getLogger(ExternalJokeResource.class.getName()).log(Level.SEVERE, null, ex);
            return "{\"info\":\"Error\"}";
        }
    }
    
    @PUT
    @Path("/favorite")
    @RolesAllowed({"user"})
    @Produces({MediaType.APPLICATION_JSON})
    public String addFavoriteExternalJoke(String input) {
        String thisuser = securityContext.getUserPrincipal().getName();
        ExternalJokeDTO joke = GSON.fromJson(input, ExternalJokeDTO.class);
        ExternalJokeDTO result = FACADE.addExternalJokeToFavoriteList(thisuser, joke);
        return GSON.toJson(result);
    }
    
    @GET
    @Path("favorites")
    @RolesAllowed("user")
    @Produces({MediaType.APPLICATION_JSON})
    public String getFavoriteList() {
        String thisuser = securityContext.getUserPrincipal().getName();
        ExternalJokesDTO allJokes = FACADE.getUserFavorites(thisuser);
        return GSON.toJson(allJokes);
    }
    

    @PUT
    @Path("/remove_favorite/{id}")
    @RolesAllowed({"user"})
    @Produces({MediaType.APPLICATION_JSON})
    public String removeFavoriteJoke(@PathParam("id") Long id) {
        String thisuser = securityContext.getUserPrincipal().getName();
        ExternalJokeDTO joke = FACADE.removeJokeFromFavoriteList(thisuser,id);
        return GSON.toJson(joke);
    }
}