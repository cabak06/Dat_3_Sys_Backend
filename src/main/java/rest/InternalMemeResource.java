package rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dto.InternalMemeDTO;
import dto.InternalMemesDTO;
import utils.EMF_Creator;
import facades.InternalMemeFacade;
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

@Path("meme")
public class InternalMemeResource {

    @Context
    SecurityContext securityContext;

    private static final EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(
            "pu",
            "jdbc:mysql://localhost:3307/SysEksamenJokes",
            "dev",
            "ax2",
            EMF_Creator.Strategy.CREATE);
    private static final InternalMemeFacade FACADE = InternalMemeFacade.getMemeFacade(EMF);
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public String demo() {
        return "{\"msg\":\"Hello World\"}";
    }

    @GET
    @Path("count")
    @Produces({MediaType.APPLICATION_JSON})
    public String getMemeCount() {
        long count = FACADE.getInternalMemeCount();
        return "{\"count\":" + count + "}";  //Done manually so no need for a DTO
    }
    
    @GET
    @Path("usermemes")
    @RolesAllowed({"user", "admin"})
    @Produces({MediaType.APPLICATION_JSON})
    public String getUserMemes() {
        String thisuser = securityContext.getUserPrincipal().getName();
        InternalMemesDTO allMemes = FACADE.getUserMemes(thisuser);
        return GSON.toJson(allMemes);
    }
    
    @POST
    @RolesAllowed("user")
    @Produces({MediaType.APPLICATION_JSON})
    @Consumes({MediaType.APPLICATION_JSON})
    public String addMeme(String meme) {
        InternalMemeDTO memeAdd = GSON.fromJson(meme, InternalMemeDTO.class);
        String thisuser = securityContext.getUserPrincipal().getName();
        memeAdd.setCreatedBy(thisuser);

        memeAdd = FACADE.addMeme(memeAdd);
        return GSON.toJson(memeAdd);
    }
}