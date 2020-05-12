package rest;

import com.google.gson.Gson;
import dto.UserDTO;
import dto.UsersDTO;
import entities.User;
import errorhandling.AuthenticationException;
import errorhandling.InvalidInputException;
import facades.UserFacade;
import java.util.List;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import utils.EMF_Creator;

@Path("user")
public class UserResource {

    private static EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    private final static Gson GSON = new Gson();
    private final static UserFacade facade = UserFacade.getUserFacade(EMF);

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

    //Just to verify if the database is setup
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("all")
    public String allUsers() {

        EntityManager em = EMF.createEntityManager();
        try {
            List<User> users = em.createQuery("select user from User user").getResultList();
            return "{\"count\": " + users.size() + "}";
        } finally {
            em.close();
        }
    }
    
    @GET
    @Path("allUsers")
    @RolesAllowed({"admin"})
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllUsers() {
        UsersDTO allJokes = facade.getUsers();
        return GSON.toJson(allJokes);
    }
    
    @GET
    @Path("allNonAdminUsers")
    @RolesAllowed({"admin"})
    @Produces(MediaType.APPLICATION_JSON)
    public String getAllNonAdminUsers() {
        UsersDTO allJokes = facade.getNonAdminUsers();
        return GSON.toJson(allJokes);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("user")
    @RolesAllowed("user")
    public String getFromUser() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to User: " + thisuser + "\"}";
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("admin")
    @RolesAllowed("admin")
    public String getFromAdmin() {
        String thisuser = securityContext.getUserPrincipal().getName();
        return "{\"msg\": \"Hello to (admin) User: " + thisuser + "\"}";
    }

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("register")
    public String registerUser(String userData) {
        UserDTO newUser = GSON.fromJson(userData, UserDTO.class);
        String error = facade.createUser(newUser);
        if (!error.isEmpty()) {
            return "{\"error\": \"" + error + "\"}";
        } else {
            return "{\"msg\": \"User registered\"}";
        }
    }
    
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes({MediaType.APPLICATION_JSON})
    @Path("nsfw")
    @RolesAllowed("user")
    public String toggleNSFW(String userData){
        UserDTO newUser = GSON.fromJson(userData, UserDTO.class);
        String thisuser = securityContext.getUserPrincipal().getName();
        newUser.setUsername(thisuser);
        return GSON.toJson(facade.updateUser(newUser))  ;
    }
    
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Path("password")
    @RolesAllowed("user")
    public String updateUserPassword(String userData) throws InvalidInputException{
        UserDTO user = GSON.fromJson(userData, UserDTO.class);
        String thisuser = securityContext.getUserPrincipal().getName();
        user.setUsername(thisuser);
        return GSON.toJson(facade.updateUserPassword(user));
    }
    
    @DELETE
    @Path("/{id}")
    @RolesAllowed({"admin"})
    public void deleteUserAsAdmin(@PathParam("id")String userName) {
        facade.deleteUser(userName);
    }
}