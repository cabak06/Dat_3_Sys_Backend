package rest;

import dto.UserDTO;
import dto.UsersDTO;
import entities.InternalJoke;
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
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

//@Disabled
public class UserResourceTest {

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";

    static final URI BASE_URI = UriBuilder.fromUri(SERVER_URL).port(SERVER_PORT).build();
    private static HttpServer httpServer;
    private static EntityManagerFactory emf;
    
    private static User user, admin, both, u1, u2;
    private static String p1, p2;
    private final static User[] USER_LIST = new User[]{user, u1, u2, admin, both};

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
            em.createQuery("delete from User").executeUpdate();
            em.createQuery("delete from Role").executeUpdate();

            Role userRole = new Role("user");
            Role adminRole = new Role("admin");
            user = new User("user", p1);
            u1 = new User("Giraf", "Pass123");
            u2 = new User("elefant", "123456");
            user.addRole(userRole);
            u1.addRole(userRole);
            u2.addRole(userRole);
            admin = new User("admin", p2);
            admin.addRole(adminRole);
            both = new User("user_admin", "test");
            both.addRole(userRole);
            both.addRole(adminRole);
            em.persist(userRole);
            em.persist(adminRole);
            em.persist(user);
            em.persist(admin);
            em.persist(both);
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
      int expectedCount = USER_LIST.length;
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
      int expectedCount = USER_LIST.length + 1;
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
      UserDTO newUser = new UserDTO(user.getUserName(), password);
      
      given()
            .contentType("application/json")
            .body(newUser)
            .when()
            .post("/user/register").then()
            .statusCode(200)
            .body("error", equalTo("Dublicate Username"));
      
      //Check database has no more users then from setup
      int expectedCount = USER_LIST.length;
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
      int expectedCount = USER_LIST.length;
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
      int expectedCount = USER_LIST.length;
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
      int expectedCount = USER_LIST.length;
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
      int expectedCount = USER_LIST.length;
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
      int expectedCount = USER_LIST.length;
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
                .delete("/user/" + u2.getUserName()).then()
                .statusCode(204);

        UsersDTO result = given()
                .contentType("application/json")
                .header("x-access-token", securityToken)
                .when()
                .get("/user/all").then()
                .statusCode(200)
                .extract().body().as(UsersDTO.class);

        int expectedLength = USER_LIST.length - 1;
        assertEquals(expectedLength, result.getUsers().size());
    }
}