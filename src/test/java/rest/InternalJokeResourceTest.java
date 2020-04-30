package rest;

import dto.ApiDTO;
import dto.InternalJokeDTO;
import dto.InternalJokesDTO;
import entities.InternalJoke;
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
    private static String p1, p2;
    private static User u1, u2;
    private static InternalJoke joke1, joke2, joke3;
    private final static InternalJoke[] JOKE_ARRAY = new InternalJoke[]{joke1, joke2, joke3};

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

            joke1 = new InternalJoke(u1, "First joke of the day");
            joke2 = new InternalJoke(u1, "2nd joke of the day");
            joke3 = new InternalJoke(u1, "Final joke");

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
            em.createNamedQuery("InternalJoke.deleteAllRows").executeUpdate();
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
    public void testAddJokeEndpoint() {
        User user = u1;

        InternalJokeDTO newJoke = new InternalJokeDTO("Haha fun");
        login(user.getUserName(), p1);

        InternalJokeDTO result = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(newJoke)
                .when()
                .post("/joke").then()
                .statusCode(200)
                .extract().body().as(InternalJokeDTO.class);

        assertTrue(result.getJokeContent().equals(newJoke.getJokeContent()));
        assertTrue(result.getCreatedBy().equals(user.getUserName()));

        EntityManager em = emf.createEntityManager();
        try {
            InternalJoke dbResult = em.find(InternalJoke.class, result.getId());
            assertTrue(result.getCreatedBy().equals(dbResult.getCreatedBy().getUserName()));
            assertTrue(result.getJokeContent().equals(dbResult.getJokeContent()));
        } catch (Exception e) {
            fail("Issues getting results from database");
        } finally {
            em.close();
        }

    }

    @Test
    public void test_Negative_AddJokeEndpoint_NotLoggedIn() {
        logOut();
        InternalJokeDTO newJoke = new InternalJokeDTO("best joke ever!");

        given()
                .contentType("application/json")
                .body(newJoke)
                .when()
                .post("/joke").then()
                .statusCode(403);
    }

    @Test
    public void test_Negative_AddJokeEndpoint_NotUserRole() {
        User user = u2; // Admin role, not User

        InternalJokeDTO newJoke = new InternalJokeDTO("new funny joke");
        login(user.getUserName(), p2);

        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .body(newJoke)
                .when()
                .post("/joke").then()
                .statusCode(401);
    }

    @Test
    public void testGetJokeList_UserLogin() {
        User user = u1;
        login(user.getUserName(), p1);

        InternalJokesDTO result = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/joke/userjokes").then()
                .statusCode(200)
                .extract().body().as(InternalJokesDTO.class);

        int expectedSize = JOKE_ARRAY.length;
        assertEquals(expectedSize, result.getJokes().size());

    }

    @Test
    public void testGetJokeList_AdminLogin() {
        User user = u2;
        login(user.getUserName(), p2);

        InternalJokesDTO result = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/joke/userjokes").then()
                .statusCode(200)
                .extract().body().as(InternalJokesDTO.class);

        int expectedSize = JOKE_ARRAY.length;
        assertEquals(expectedSize, result.getJokes().size());
    }

    @Test
    public void test_Negative_GetJokeList_NotLoggedIn() {
        given()
                .contentType("application/json")
                .when()
                .get("/joke/userjokes").then()
                .statusCode(403);
    }
    
    
    // statusCode 204 er korrekt fordi sletningen finder sted, men resultatet ikke returneres til os.
    @Test
    public void testDeleteJokeEndpoint_asAdmin() {
        User user = u2;
        login(user.getUserName(), p2);

        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .delete("/joke/"+joke3.getId()).then()
                .statusCode(204);

        InternalJokesDTO result = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/joke/userjokes").then()
                .statusCode(200)
                .extract().body().as(InternalJokesDTO.class);
        
        int expectedLength = JOKE_ARRAY.length-1;
        assertEquals(expectedLength, result.getJokes().size());
    }
    
    @Test
    public void negativeTestDeleteJokeEndpoint_notAdmin() {
        User user = u1; //logged in as a regular user not admin
        login(user.getUserName(), p1);

        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .delete("/joke/"+joke3.getId()).then()
                .statusCode(401);
    }
    
    @Test
    public void testGetOwnJokes_UserLogin() {
        User user = u1;
        login(user.getUserName(), p1);

        InternalJokesDTO result = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/joke/ownjokes").then()
                .statusCode(200)
                .extract().body().as(InternalJokesDTO.class);

        int expectedSize = JOKE_ARRAY.length;
        assertEquals(expectedSize, result.getJokes().size());
    }
}