package rest;

import dto.ApiDTO;
import dto.InternalJokeDTO;
import dto.InternalJokesDTO;
import dto.InternalMemeDTO;
import dto.InternalMemesDTO;
import entities.InternalJoke;
import entities.InternalMeme;
import entities.Role;
import entities.User;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.List;
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
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

public class InternalMemeResourceTest {

    public InternalMemeResourceTest() {
    }

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static String p1, p2;
    private static User u1, u2;
    private static InternalMeme meme1, meme2, meme3;
    private static InternalMeme[] memeArray;

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;

    static HttpServer startServer() {
        ResourceConfig rc = ResourceConfig.forApplication(new ApplicationConfig());
        return GrizzlyHttpServerFactory.createHttpServer(BASE_URI, rc);
    }

    @BeforeAll
    public static void setUpClass() {
        EMF_Creator.startREST_TestWithDB();
        emf = EMF_Creator.createEntityManagerFactory(EMF_Creator.DbSelector.TEST, EMF_Creator.Strategy.DROP_AND_CREATE);

        httpServer = startServer();
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

            em.createNamedQuery("InternalMeme.deleteAllRows").executeUpdate();
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

            meme1 = new InternalMeme(u1, "First meme of the day", "title of first meme");
            meme2 = new InternalMeme(u1, "2nd meme of the day", "title of second meme");
            meme3 = new InternalMeme(u1, "Final joke", "title of third meme", true);

            em.persist(userRole);
            em.persist(adminRole);
            em.persist(u1);
            em.persist(u2);
            em.persist(meme1);
            em.persist(meme2);
            em.persist(meme3);

            em.getTransaction().commit();
        } finally {
            em.close();
        }
        
        memeArray = new InternalMeme[]{meme1, meme2, meme3};
    }

    @AfterAll
    public static void closeTestServer() {
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    @AfterEach
    public void tearDown() {
        logOut();
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("InternalMeme.deleteAllRows").executeUpdate();
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
    public void testGetMemeList_UserLogin_Where_NSFW_Active() {
        User user = u2;
        if(!user.isNsfwIsActive()){
            fail("User did not have NSFW active");
        }
        login(user.getUserName(), p2);

        InternalMemesDTO result = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/meme/usermemes").then()
                .statusCode(200)
                .extract().body().as(InternalMemesDTO.class);

        int expectedSize = memeArray.length;
        assertEquals(expectedSize, result.getMemes().size());
    }

    @Test
    public void testGetMemeList_UserLogin_Where_NSFW_Not_Active() {
        User user = u1;
        if(user.isNsfwIsActive()){
            fail("User did have NSFW active");
        }
        login(user.getUserName(), p1);

        InternalMemesDTO result = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/meme/usermemes").then()
                .statusCode(200)
                .extract().body().as(InternalMemesDTO.class);

        int expectedSize = 0;
        for (InternalMeme meme : memeArray) {
            if(!meme.isNsfw()){
                expectedSize++;
            }
        }
        if(memeArray.length == expectedSize){
            fail("Test did not contain any jokes that was NSFW");
        }
        assertEquals(expectedSize, result.getMemes().size());
    }

    @Test
    public void testGetMemeList_AdminLogin() {
        User user = u2;
        login(user.getUserName(), p2);

        InternalMemesDTO result = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/meme/usermemes").then()
                .statusCode(200)
                .extract().body().as(InternalMemesDTO.class);

        int expectedSize = memeArray.length;
        assertEquals(expectedSize, result.getMemes().size());
    }

    @Test
    public void test_Negative_GetMemeList_NotLoggedIn() {
        given()
                .contentType("application/json")
                .when()
                .get("/meme/usermemes").then()
                .statusCode(403);
    }
    
    @Test
    public void testAddMemeEndpoint() {
        User user = u1;

        InternalMemeDTO newMeme = new InternalMemeDTO("Hahafun.com");
        login(user.getUserName(), p1);

        InternalMemeDTO result = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(newMeme)
                .when()
                .post("/meme").then()
                .statusCode(200)
                .extract().body().as(InternalMemeDTO.class);

        assertTrue(result.getCreatedBy().equals(user.getUserName()));
        assertTrue(result.getPicturePath().equals(newMeme.getPicturePath()));

        EntityManager em = emf.createEntityManager();
        try {
            InternalMeme dbResult = em.find(InternalMeme.class, result.getId());
            assertTrue(result.getCreatedBy().equals(dbResult.getCreatedBy().getUserName()));
            assertTrue(result.getPicturePath().equals(dbResult.getPicturePath()));
        } catch (Exception e) {
            fail("Issues getting results from database");
        } finally {
            em.close();
        }
    }
    
    @Test
    public void testDeleteUserMeme_asAdmin() {
        User user = u2;
        login(user.getUserName(), p2);

        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .delete("/meme/delete/" + meme3.getId()).then()
                .statusCode(204);

        InternalMemesDTO result = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/meme/usermemes").then()
                .statusCode(200)
                .extract().body().as(InternalMemesDTO.class);

        int expectedLength = memeArray.length - 1;
        assertEquals(expectedLength, result.getMemes().size());
    }
    
    @Test
    public void testDeleteJokeEndpoint_asAdminEndpointDoesNotExist() {
        User user = u2;
        login(user.getUserName(), p2);
        long memeID = meme3.getId()+4;

        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .delete("/meme/delete/" + memeID).then()
                .statusCode(204);

        InternalMemesDTO result = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/meme/usermemes").then()
                .statusCode(200)
                .extract().body().as(InternalMemesDTO.class);

        int expectedLength = memeArray.length;
        assertEquals(expectedLength, result.getMemes().size());
    }

    @Test
    public void negativeTestDeleteJokeEndpoint_notAdmin() {
        User user = u1; //logged in as a regular user not admin
        login(user.getUserName(), p1);

        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .delete("/meme/delete/" + meme3.getId()).then()
                .statusCode(401);
    }
}