package facades;

import dto.InternalJokeDTO;
import dto.InternalJokesDTO;
import utils.EMF_Creator;
import entities.InternalJoke;
import entities.User;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

//Uncomment the line below, to temporarily disable this test
//@Disabled
public class InternalJokeFacadeTest {

    private static EntityManagerFactory emf;
    private static InternalJokeFacade facade;
    private static Long highestId;
    private static InternalJoke joke1, joke2;
    private static InternalJoke[] jokes;
    private static User user1, user2;

    public InternalJokeFacadeTest() {
    }

    //@BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(
                "pu",
                "jdbc:mysql://localhost:3307/startcode_test",
                "dev",
                "ax2",
                EMF_Creator.Strategy.CREATE);
        facade = InternalJokeFacade.getFacadeExample(emf);
    }

    /*   **** HINT **** 
        A better way to handle configuration values, compared to the UNUSED example above, is to store those values
        ONE COMMON place accessible from anywhere.
        The file config.properties and the corresponding helper class utils.Settings is added just to do that. 
        See below for how to use these files. This is our RECOMENDED strategy
     */
    @BeforeAll
    public static void setUpClassV2() {
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.DROP_AND_CREATE);
        facade = InternalJokeFacade.getFacadeExample(emf);
    }

    @AfterAll
    public static void tearDownClass() {
//        Clean up database after test is done or use a persistence unit with drop-and-create to start up clean on every test
    }

    // Setup the DataBase in a known state BEFORE EACH TEST
    //TODO -- Make sure to change the script below to use YOUR OWN entity class
    //User createdBy, String jokeContent, String createdDate, String lastEdited
    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("InternalJoke.deleteAllRows").executeUpdate();
            user1 = new User("kim", "Password123", true);
            user2 = new User("larsen", "VerySecureP4ssword");
            joke1 = new InternalJoke(user1, "Haha");
            joke2 = new InternalJoke(user1, "jokeContent", true);
            em.persist(user1);
            em.persist(user2);
            em.persist(joke1);
            em.persist(joke2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        jokes = new InternalJoke[]{joke1, joke2};
        highestId = 0L;
        for (InternalJoke joke : jokes) {
            if (joke.getId() > highestId) {
                highestId = joke.getId();
            }
        }
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

    // TODO: Delete or change this method 
    @Test
    public void testAFacadeMethod() {
        assertEquals(2, facade.getInternalJokeCount(), "Expects two rows in the database");
    }

    @Test
    public void testAddJoke() {
        InternalJokeDTO jokeDTO = new InternalJokeDTO(user1.getUserName(), "Funny stuff");
        Long expectedId = highestId + 1;

        InternalJokeDTO result = facade.addJoke(jokeDTO);

        assertTrue(result.getJokeContent().equals(jokeDTO.getJokeContent()));
        assertTrue(result.getCreatedBy().equals(jokeDTO.getCreatedBy()));
        assertEquals(expectedId, result.getId());

    }

    @Test
    public void testDeleteJoke() {
        facade.deleteUserJoke(joke1.getId());

        EntityManager em = emf.createEntityManager();
        try {
            List<InternalJoke> dbResult = em.createQuery("Select i FROM InternalJoke i", InternalJoke.class).getResultList();
            assertEquals(dbResult.size(), jokes.length - 1);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            em.close();
        }
    }

    @Test
    public void testGetJokes_nsfwActive() {
        User user = user1;
        if (!user.isNsfwIsActive()) {
            fail("User for test did not have NSFW active");
        }
        InternalJokesDTO result = facade.getUserJokes(user.getUserName());

        int expectedSize = jokes.length;
        assertEquals(expectedSize, result.getJokes().size());
    }

    @Test
    public void testGetJokes_nsfwNotActive() {
        User user = user2;
        if (user.isNsfwIsActive()) {
            fail("User for test did have NSFW active");
        }
        int expectedSize = 0;
        for (InternalJoke joke : jokes) {
            if (joke.isNsfw()) {
                expectedSize++;
            }
        }

        InternalJokesDTO result = facade.getUserJokes(user.getUserName());

        assertEquals(expectedSize, result.getJokes().size());
        for (InternalJokeDTO joke : result.getJokes()) {
            assertFalse(joke.isNsfw());
        }
    }
    
    @Test
    public void testGetUserJokesBySpecificUser() {
        User user = user1;
        facade.getUserJokesForSpecificUser(user.getUserName());
    }
}
