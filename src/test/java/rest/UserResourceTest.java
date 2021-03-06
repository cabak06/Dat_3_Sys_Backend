package rest;

import dto.InternalJokesDTO;
import dto.UserDTO;
import dto.UsersDTO;
import entities.InternalJoke;
import entities.InternalMeme;
import entities.User;
import entities.Role;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.parsing.Parser;
import java.net.URI;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import static org.hamcrest.Matchers.equalTo;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

//@Disabled
public class UserResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    
    private static User user1, user2, user3, admin, both;
    private static InternalJoke joke1, joke2;
    private static InternalMeme meme1, meme2;
    private static String p1, p2;
    private final static User[] USER_ARRAY = new User[]{user1, user2, user3, admin, both};
    private final static InternalJoke[] JOKE_ARRAY = new InternalJoke[]{joke1, joke2};

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

    @AfterAll
    public static void closeTestServer() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createQuery("delete from InternalJoke").executeUpdate();
            em.createQuery("delete from InternalMeme").executeUpdate();
            em.createQuery("delete from User").executeUpdate();
            em.createQuery("delete from Role").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
    }

    // Setup the DataBase (used by the test-server and this test) in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the EntityClass used below to use YOUR OWN (renamed) Entity class
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            //Delete existing users and roles to get a "fresh" database
            em.createQuery("delete from InternalJoke").executeUpdate();
            em.createQuery("delete from InternalMeme").executeUpdate();
            em.createQuery("delete from User").executeUpdate();
            em.createQuery("delete from Role").executeUpdate();
            

            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            user1 = new User("user1", p1);
            user1.addRole(userRole);
            user2 = new User("user2", p1);
            user2.addRole(userRole);
            user3 = new User("user3", p1);
            user3.addRole(userRole);
            admin = new User("admin", p2);
            admin.addRole(adminRole);
            both = new User("user_admin", "test");
            both.addRole(userRole);
            both.addRole(adminRole);
            joke1 = new InternalJoke(user1, "Funny joke");
            joke2 = new InternalJoke(user3, "Another funny joke");
            meme1 = new InternalMeme(user2, "path1", "MemeTitle1");
            meme2 = new InternalMeme(user3, "path2", "MemeTitle2");
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user1);
            em.persist(user2);
            em.persist(user3);
            em.persist(admin);
            em.persist(both);
            em.persist(joke1);
            user3.getFavoriteJokes().add(joke1);
            em.persist(joke2);
            em.persist(meme1);
            em.persist(meme2);
            System.out.println("Saved test data to database");
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
            .then()
            .extract().path("token");
      System.out.println("TOKEN ---> "+securityToken);
  }

  private void logOut() {
    securityToken = null;
  }

  @Test
  public void serverIsRunning() {
    System.out.println("Testing is server UP");
    given().when().get("/user").then().statusCode(200);
  }
  
  @Test
  public void testAllUsersCount(){
      int expectedCount = USER_ARRAY.length;
      given()
            .contentType("application/json")
            .when()
            .get("/user/all").then()
            .statusCode(200)
            .body("count", equalTo(expectedCount));
      
  }
  
  @Test
  public void testRegisterUser(){
      String password = "Dragon7";
      UserDTO newUser = new UserDTO("newGuy", password);
      
      given()
            .contentType("application/json")
            .body(newUser)
            .when()
            .post("/user/register").then()
            .statusCode(200)
            .body("msg", equalTo("User registered"));
      
      //Check database has one more user
      int expectedCount = USER_ARRAY.length + 1;
      given()
            .contentType("application/json")
            .when()
            .get("/user/all").then()
            .statusCode(200)
            .body("count", equalTo(expectedCount));
      
      //Check that new user can log in
      logOut();
      assertNull(securityToken);
      
      login(newUser.getUsername(), password);
      assertNotNull(securityToken);
      
      //Check that new user cannot be logged into with wrong password
      login(newUser.getUsername(), password + "salt");
      assertNull(securityToken);
  }
  
  @Test
  public void test_Negative_RegisterUser_DublicateUsername(){
      String password = "Dragon7";
      UserDTO newUser = new UserDTO(user1.getUserName(), password);
      
      given()
            .contentType("application/json")
            .body(newUser)
            .when()
            .post("/user/register").then()
            .statusCode(200)
            .body("error", equalTo("Dublicate Username"));
      
      //Check database has no more users then from setup
      int expectedCount = USER_ARRAY.length;
      given()
            .contentType("application/json")
            .when()
            .get("/user/all").then()
            .statusCode(200)
            .body("count", equalTo(expectedCount));
  }
  
  @Test
  public void test_Negative_RegisterUser_InvalidPassword_ToShort(){
      String password = "Dr3";
      UserDTO newUser = new UserDTO("NewGuy", password);
      
      given()
            .contentType("application/json")
            .body(newUser)
            .when()
            .post("/user/register").then()
            .statusCode(200)
            .body("error", equalTo("Password in invalid. Need 1 upper- and lower-case, one number, "
                        + "and needs to be between 5 and 20 characters."));
      
      //Check database has no more users then from setup
      int expectedCount = USER_ARRAY.length;
      given()
            .contentType("application/json")
            .when()
            .get("/user/all").then()
            .statusCode(200)
            .body("count", equalTo(expectedCount));
  }
  
  @Test
  public void test_Negative_RegisterUser_InvalidPassword_ToLong(){
      String password = "ThisIsTheLongestPasswordEverAnd4Ever";
      UserDTO newUser = new UserDTO("NewGuy", password);
      
      given()
            .contentType("application/json")
            .body(newUser)
            .when()
            .post("/user/register").then()
            .statusCode(200)
            .body("error", equalTo("Password in invalid. Need 1 upper- and lower-case, one number, "
                        + "and needs to be between 5 and 20 characters."));
      
      //Check database has no more users then from setup
      int expectedCount = USER_ARRAY.length;
      given()
            .contentType("application/json")
            .when()
            .get("/user/all").then()
            .statusCode(200)
            .body("count", equalTo(expectedCount));
  }
  
  @Test
  public void test_Negative_RegisterUser_InvalidPassword_NoLowerCase(){
      String password = "PASSWORD123";
      UserDTO newUser = new UserDTO("NewGuy", password);
      
      given()
            .contentType("application/json")
            .body(newUser)
            .when()
            .post("/user/register").then()
            .statusCode(200)
            .body("error", equalTo("Password in invalid. Need 1 upper- and lower-case, one number, "
                        + "and needs to be between 5 and 20 characters."));
      
      //Check database has no more users then from setup
      int expectedCount = USER_ARRAY.length;
      given()
            .contentType("application/json")
            .when()
            .get("/user/all").then()
            .statusCode(200)
            .body("count", equalTo(expectedCount));
  }
  
  @Test
  public void test_Negative_RegisterUser_InvalidPassword_NoUpperCase(){
      String password = "password123";
      UserDTO newUser = new UserDTO("NewGuy", password);
      
      given()
            .contentType("application/json")
            .body(newUser)
            .when()
            .post("/user/register").then()
            .statusCode(200)
            .body("error", equalTo("Password in invalid. Need 1 upper- and lower-case, one number, "
                        + "and needs to be between 5 and 20 characters."));
      
      //Check database has no more users then from setup
      int expectedCount = USER_ARRAY.length;
      given()
            .contentType("application/json")
            .when()
            .get("/user/all").then()
            .statusCode(200)
            .body("count", equalTo(expectedCount));
  }
  
  @Test
  public void test_Negative_RegisterUser_InvalidPassword_NoNumber(){
      String password = "PassWord";
      UserDTO newUser = new UserDTO("NewGuy", password);
      
      given()
            .contentType("application/json")
            .body(newUser)
            .when()
            .post("/user/register").then()
            .statusCode(200)
            .body("error", equalTo("Password in invalid. Need 1 upper- and lower-case, one number, "
                        + "and needs to be between 5 and 20 characters."));
      
      //Check database has no more users then from setup
      int expectedCount = USER_ARRAY.length;
      given()
            .contentType("application/json")
            .when()
            .get("/user/all").then()
            .statusCode(200)
            .body("count", equalTo(expectedCount));
  }
  
  @Test
    public void testDeleteUserEndpoint_asAdmin() {
        User administrator = admin;
        String password = p2;
        login(administrator.getUserName(), password);

        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .delete("/user/" + user1.getUserName()).then()
                .statusCode(204);

        UsersDTO result = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/user/allUsers").then()
                .statusCode(200)
                .extract().body().as(UsersDTO.class);
        
        InternalJokesDTO resultJoke = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/joke/userjokes").then()
                .statusCode(200)
                .extract().body().as(InternalJokesDTO.class);
        
        int expectedLengthUsers = USER_ARRAY.length - 1;
        int expectedLengthJokes = JOKE_ARRAY.length - 1;
        assertEquals(expectedLengthUsers, result.getUsers().size());
        assertEquals(expectedLengthJokes, resultJoke.getJokes().size());
    }
    @Test
    public void test_deleteOfUserWithJokeForFav() {
        User administrator = admin;
        String password = p2;
        login(administrator.getUserName(), password);
        
        given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .delete("/user/" + user1.getUserName()).then()
                .statusCode(204);
        logOut(); //For admin
        
        User user = user3;
        String uPassword = p1;
        login(user.getUserName(), uPassword);
        InternalJokesDTO resultFavJoke = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/joke/favorites").then()
                .statusCode(200)
                .extract().body().as(InternalJokesDTO.class);
        
        assertEquals(0, resultFavJoke.getJokes().size());
    }
}