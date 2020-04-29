package rest;

import dto.ApiDTO;
import entities.InternalJoke;
import io.restassured.RestAssured;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;
import io.restassured.parsing.Parser;
import java.net.URI;
import java.util.Objects;
import javax.persistence.EntityManagerFactory;
import javax.ws.rs.core.UriBuilder;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.junit.jupiter.api.AfterAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;

public class ExternalJokeResourceTest {

    public ExternalJokeResourceTest() {
    }

    private static final int SERVER_PORT = 7777;
    private static final String SERVER_URL = "http://localhost/api";
    private static InternalJoke r1, r2;

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

    @AfterAll
    public static void closeTestServer() {
        //Don't forget this, if you called its counterpart in @BeforeAll
        EMF_Creator.endREST_TestWithDB();
        httpServer.shutdownNow();
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
}
