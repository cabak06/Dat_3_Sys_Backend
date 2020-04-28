package rest;

import dto.ApiDTO;
import dto.InternalJokeDTO;
import entities.InternalJoke;
import entities.User;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

public class InternalJokeResourceTest {

    public InternalJokeResourceTest() {
    }

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static InternalJoke r1, r2;
    private static User u1;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        //This method must be called before you request the EntityManagerFactory
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.DROP_AND_CREATE);

        httpServer = startServer();
        //Setup RestAssured
        RestAssured.baseURI = SERVER_URL;
        RestAssured.port = SERVER_PORT;
        RestAssured.defaultParser = Parser.JSON;

    }
    
    @BeforeEach
    public void setUp() {
        u1 = new User("Lars", "54321");
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.persist(u1);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    @AfterAll
    public static void closeTestServer() {
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }
    
    @AfterEach
    public void tearDown() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("InternalJoke.deleteAllRows").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    /* on each of the 8 endpoints we first test whether the data is not null
    and then whether the endpoint's empty. These tests are very hardcoded,
    but as the endpoints return randomized data it's a logical solution */
    @Test
    public void testExternalAPIEndpoint() {
        InternalJokeDTO newJoke = new InternalJokeDTO(u1.getUserName(), "Haha fun");
        InternalJokeDTO result = given()
                .contentType("application/json").body(newJoke)
                .when()
                .post("/joke").then()
                .statusCode(200)
                .extract().body().as(InternalJokeDTO.class);
        
        assertTrue(result.getJokeContent().equals(newJoke.getJokeContent()));
        assertTrue(result.getCreatedBy().equals(newJoke.getCreatedBy()));
        
        EntityManager em = emf.createEntityManager();
        try {
            InternalJoke dbResult = em.find(InternalJoke.class, result.getId());
            assertTrue(result.getCreatedBy().equals(dbResult.getCreatedBy().getUserName()));
            assertTrue(result.getJokeContent().equals(dbResult.getJokeContent()));
        } catch(Exception e) {
            fail("Issues getting results from database");
        } finally {
            em.close();
        } 
        

    }
}
