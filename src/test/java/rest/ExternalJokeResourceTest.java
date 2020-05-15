package rest;

import dto.ApiDTO;
import dto.ExternalJokeDTO;
import entities.ExternalJoke;
import entities.InternalJoke;
import entities.Role;
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

public class ExternalJokeResourceTest {

    public ExternalJokeResourceTest() {
    }

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static String p1, p2;
    private static User u1, u2;
    private static ExternalJoke joke1, joke2, joke3;
    private static ExternalJoke[] jokeArray;

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
        logOut();
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("InternalJoke.deleteAllRows").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            p1 = "54321";
            p2 = "Monkey";
            u1 = new User("Lars", p1);
            u2 = new User("Per", p2);
            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            u1.addRole(userRole);
            u2.addRole(adminRole);
            joke1 = new ExternalJoke("haha", true, "haha.com");
            joke2 = new ExternalJoke("lol", true, "lol.com");
            joke3 = new ExternalJoke("rofl", false, "rofl.com");
            u1.getFavoriteExternalJokes().add(joke3);
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(u1);
            em.persist(u2);
            em.persist(joke1);
            em.persist(joke2);
            em.persist(joke3);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        jokeArray = new ExternalJoke[]{joke1, joke2, joke3};
    }

    @AfterAll
    public static void closeTestServer() {
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }
    
    @AfterEach
    public void tearDown() {
        logOut();
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("ExternalJoke.deleteAllRows").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.createQuery("DELETE FROM Role").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    //This is how we hold on to the token after login, similar to that a client must store the token somewhere
    private static String securityToken;

    //Utility method to login and set the returned securityToken
    private static void login(String role, String password) {
        String json = String.format("{username: \"%s\", password: \"%s\"}", role, password);
        securityToken = given()
                .contentType("application/json")
                .body(json)
                //.when().post("/api/login")
                .when().post("/login")
                .then().log().body()
                .extract().path("token");
        System.out.println("TOKEN ---> " + securityToken);
    }

    private void logOut() {
        securityToken = null;
    }

    @Test
    public void testExternalAPIEndpoint() {
        ApiDTO result = given()
                .contentType("application/json")
                .accept(ContentType.JSON)
                .when()
                .get("/externalJoke/random").then()
                .statusCode(200)
                .extract().body().as(ApiDTO.class);
        assertTrue(!Objects.isNull(result.getChuckJoke()));
        assertTrue(!result.getChuckJoke().isEmpty());
        assertTrue(!Objects.isNull(result.getChuckJokeID()));
        assertTrue(!result.getChuckJokeID().isEmpty());

        assertTrue(!Objects.isNull(result.getDadJoke()));
        assertTrue(!result.getDadJoke().isEmpty());
        assertTrue(!Objects.isNull(result.getDadJokeID()));
        assertTrue(!result.getDadJokeID().isEmpty());
    }
    
    @Test
    public void testAddFavoriteExternalJoke() {
        User user = u1;

        ExternalJokeDTO newJoke = new ExternalJokeDTO("Haha fun", true, "lol.io");
        login(user.getUserName(), p1);
        
            given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(newJoke)
                .accept(ContentType.JSON)
                .when()
                .put("/externalJoke/favorite").then()
                .statusCode(200)
                .extract().body().as(ExternalJokeDTO.class);
        
        EntityManager em = emf.createEntityManager();
        try {
        User dbUser = em.find(User.class, user.getUserName());
        int expectedResult = 2;
        assertEquals(expectedResult, dbUser.getFavoriteExternalJokes().size());
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            em.close();
        }
    }
    
    @Test
    public void testGetFavoriteList() {
        User user = u1;
        login(user.getUserName(), p1);
        
            given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .accept(ContentType.JSON)
                .when()
                .get("/externalJoke/favorites").then()
                .statusCode(200)
                .extract().body().as(ExternalJokeDTO.class);
            
        EntityManager em = emf.createEntityManager();
        try {
        User dbUser = em.find(User.class, user.getUserName());
        int expectedResult = 1;
        assertEquals(expectedResult, dbUser.getFavoriteExternalJokes().size());
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            em.close();
        }
    }
    
    @Test
    public void testRemoveFavoriteJoke() {
        User user = u1;
        login(user.getUserName(), p1);
        
            given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .accept(ContentType.JSON)
                .when()
                .put("/externalJoke/remove_favorite/"+joke3.getId()).then()
                .statusCode(200)
                .extract().body().as(ExternalJokeDTO.class);
            
        EntityManager em = emf.createEntityManager();
        try {
        User dbUser = em.find(User.class, user.getUserName());
        int expectedResult = 0;
        assertEquals(expectedResult, dbUser.getFavoriteExternalJokes().size());
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            em.close();
        }
    }
}