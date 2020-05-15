package facades;

import dto.ExternalJokeDTO;
import dto.ExternalJokesDTO;
import entities.ExternalJoke;
import entities.User;
import errorhandling.InvalidInputException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.EMF_Creator;
import utils.EMF_Creator.DbSelector;
import utils.EMF_Creator.Strategy;

public class ExternalJokeFacadeTest {
    
    private static EntityManagerFactory emf;
    private static ExternalJokeFacade facade;
    private static Long highestId;
    private static ExternalJoke joke1, joke2;
    private static ExternalJoke[] jokes;
    private static User user1, user2;

    public ExternalJokeFacadeTest() {
    }
    
    //@BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(
                "pu",
                "jdbc:mysql://localhost:3307/startcode_test",
                "dev",
                "ax2",
                EMF_Creator.Strategy.CREATE);
        facade = ExternalJokeFacade.getJokeFacade(emf);
    }

    @BeforeAll
    public static void setUpClassV2() {
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.DROP_AND_CREATE);
        facade = ExternalJokeFacade.getJokeFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("InternalJoke.deleteAllRows").executeUpdate();
            em.createNamedQuery("ExternalJoke.deleteAllRows").executeUpdate();
            em.createQuery("DELETE FROM User");
            user1 = new User("kim", "Password123", true);
            user2 = new User("larsen", "VerySecureP4ssword");
            joke1 = new ExternalJoke("Haha", true, "haha.com");
            joke2 = new ExternalJoke("lol", false, "lol.dk");
            user2.getFavoriteExternalJokes().add(joke2);
            em.persist(user1);
            em.persist(user2);
            em.persist(joke1);
            em.persist(joke2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        jokes = new ExternalJoke[]{joke1, joke2};
        highestId = 0L;
        for (ExternalJoke joke : jokes) {
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
            em.createNamedQuery("ExternalJoke.deleteAllRows").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    @Test
    public void testAddExternalJokeToFavoriteList() {
        User user = user1;
        ExternalJokeDTO joke = new ExternalJokeDTO("much fun", true, "lol.com");
        facade.addExternalJokeToFavoriteList(user.getUserName(), joke);
        
        EntityManager em = emf.createEntityManager();
        try {
            User dbUser = em.find(User.class, user.getUserName());
            List<ExternalJoke> dbResult = dbUser.getFavoriteExternalJokes();
            
            List<ExternalJoke> dbResults = em.createQuery("SELECT e FROM ExternalJoke e", ExternalJoke.class).getResultList();
            
            int ExpectedResultForUser = 1;
            int ExpectedResultForAll = 3;
            assertEquals(ExpectedResultForUser, dbResult.size());
            assertEquals(ExpectedResultForAll, dbResults.size());
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            em.close();
        }
    }
    
    @Test
    public void testAddExternalJokeToFavoriteList_JokeAddressAlreadyExists() {
        User user = user1;
        ExternalJokeDTO joke = new ExternalJokeDTO("much fun", true, "lol.dk");
        facade.addExternalJokeToFavoriteList(user.getUserName(), joke);
        
        EntityManager em = emf.createEntityManager();
        try {
            User dbUser = em.find(User.class, user.getUserName());
            List<ExternalJoke> dbResult = dbUser.getFavoriteExternalJokes();
            
            List<ExternalJoke> dbResults = em.createQuery("SELECT e FROM ExternalJoke e", ExternalJoke.class).getResultList();
            
            int ExpectedResultForUser = 1;
            int ExpectedResultForAll = 2;
            assertEquals(ExpectedResultForUser, dbResult.size());
            assertEquals(ExpectedResultForAll, dbResults.size());
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            em.close();
        }
    }
    
    @Test
    public void testGetUserFavorites() {
        User user = user2;
        List<ExternalJoke> results = user.getFavoriteExternalJokes();
        
        int expectedResult = 1;
        
        assertEquals(expectedResult, results.size());
    }
    
    @Test
    public void testRemoveJokeFromFavoriteList() {
        User user = user2;
        user.getFavoriteExternalJokes().remove(joke2);
        
        List<ExternalJoke> results = user.getFavoriteExternalJokes();
        
        int expectedResult = 0;
        assertEquals(expectedResult, results.size());
    }
}