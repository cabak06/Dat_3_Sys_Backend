/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rest;

import com.google.gson.Gson;
import dto.AllMemeDTO;
import dto.ApiDTO;
import dto.DarkMemeDTO;
import dto.RedditMemeDTO;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.security.RolesAllowed;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PUT;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.SecurityContext;
import utils.EMF_Creator;
import utils.HttpUtils;

/**
 * REST Web Service
 *
 * @author andre
 */
@Path("externalMeme")
public class ExternalMemeResource {

    private static EntityManagerFactory EMF = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.DEV, EMF_Creator.Strategy.CREATE);

    @Context
    private UriInfo context;

    @Context
    SecurityContext securityContext;

    private final static Gson GSON = new Gson();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getInfoForAll() {
        return "{\"msg\":\"Hello anonymous\"}";
    }

    @GET
    //@RolesAllowed("user")
    @Produces(MediaType.APPLICATION_JSON)
    @Path("random")
    public String getFromExternalAPI() {
        String[] fetchUrls = new String[]{
            RedditMemeDTO.getRANDOM_URL(),
            DarkMemeDTO.getRANDOM_URL()
        };
        String[] fetched = new String[fetchUrls.length];
        ExecutorService workingJack = Executors.newFixedThreadPool(fetchUrls.length);
        
        try {
            for (int i = 0; i < fetchUrls.length; i++) {
                final int n = i;
                Runnable task = () -> {
                    try {
                        fetched[n] = HttpUtils.fetchData(fetchUrls[n]);
                    } catch (IOException ex) {
                        Logger.getLogger(ExternalJokeResource.class.getName()).log(Level.SEVERE, null, ex);
                    }
                };
                workingJack.submit(task);
            }

            workingJack.shutdown();
            workingJack.awaitTermination(5, TimeUnit.SECONDS);
            
            RedditMemeDTO redditMeme = GSON.fromJson(fetched[0], RedditMemeDTO.class);
            DarkMemeDTO darkMeme = GSON.fromJson(fetched[1], DarkMemeDTO.class);
            
            AllMemeDTO apis = new AllMemeDTO(redditMeme, darkMeme);
            return GSON.toJson(apis);
        } catch (InterruptedException ex) {
            Logger.getLogger(ExternalJokeResource.class.getName()).log(Level.SEVERE, null, ex);
            return "{\"info\":\"Error\"}";
        }
    }
}
