package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.InternalJokeDTO;
import dto.InternalJokesDTO;
import entities.User;
import utils.EMF_Creator;
import facades.InternalJokeFacade;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;

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
    private static final InternalJokeFacade FACADE = InternalJokeFacade.getJokeFacade(EMF);
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
        return "{\"count\":" + count + "}";  //Done manually so no need for a DTO
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

    @GET
    @Path("userjokes")
    @RolesAllowed({"user", "admin"})
    @Produces({MediaType.APPLICATION_JSON})
    public String getUserJokes() {
        String thisuser = securityContext.getUserPrincipal().getName();
        InternalJokesDTO allJokes = FACADE.getUserJokes(thisuser);
        return GSON.toJson(allJokes);
    }
    
    @GET
    @Path("ownjokes")
    @RolesAllowed({"user"})
    @Produces({MediaType.APPLICATION_JSON})
    public String getUserJokesBySpecificUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        InternalJokesDTO allJokes = FACADE.getUserJokesForSpecificUser(thisuser);
        return GSON.toJson(allJokes);
    }
    
    @DELETE
    @Path("/{id}")
    @RolesAllowed({"admin"})
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public void deleteUserJokesAsAdmin(@PathParam("id")long id) {
        FACADE.deleteUserJokeAsAdmin(id);
    }
    
    @DELETE
    @Path("/userdelete/{id}")
    @RolesAllowed({"user"})
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public void deleteUserJokeAsUser(@PathParam("id")Long id) {
        String thisuser = securityContext.getUserPrincipal().getName();
        FACADE.deleteUserJokeAsUser(thisuser, id);
    }
    
    @PUT
    @Path("/editjoke")
    @RolesAllowed({"user"})
    @Produces({MediaType.APPLICATION_JSON})
    public String editOwnUserJokesAsUser(String joke) {
        String thisuser = securityContext.getUserPrincipal().getName();
        
        InternalJokeDTO editedJoke = GSON.fromJson(joke, InternalJokeDTO.class);
        FACADE.editUserJoke(thisuser, editedJoke);
        return GSON.toJson(editedJoke);
    }
    
 
    @PUT
    @Path("/favorite/{id}")
    @RolesAllowed({"user"})
    @Produces({MediaType.APPLICATION_JSON})
    public String addFavoriteJoke(@PathParam("id") Long id) {
    String thisuser = securityContext.getUserPrincipal().getName();
    InternalJokeDTO joke = FACADE.addJokeToFavoriteList(thisuser,id);
    return GSON.toJson(joke);
    }
    
    @GET
    @Path("favorites")
    @RolesAllowed("user")
    @Produces({MediaType.APPLICATION_JSON})
    public String getFavoriteList() {
        String thisuser = securityContext.getUserPrincipal().getName();

        InternalJokesDTO allJokes = FACADE.getUserFavorites(thisuser);
        return GSON.toJson(allJokes);
    }
    

    @PUT
    @Path("/remove_favorite/{id}")
    @RolesAllowed({"user"})
    @Produces({MediaType.APPLICATION_JSON})
    public String removeFavoriteJoke(@PathParam("id") Long id) {
    String thisuser = securityContext.getUserPrincipal().getName();
    InternalJokeDTO joke = FACADE.removeJokeFromFavoriteList(thisuser,id);
    return GSON.toJson(joke);
    }
    
    
}