package facades;

import dto.InternalMemeDTO;
import dto.InternalMemesDTO;
import utils.EMF_Creator;
import entities.InternalMeme;
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

public class UserFacadeTest {

    private static EntityManagerFactory emf;
    private static UserFacade facade;
    private static Long highestId;
    private static User user1, user2;
    private static User[] users;

    public UserFacadeTest() {
    }

    //@BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(
                "pu",
                "jdbc:mysql://localhost:3307/startcode_test",
                "dev",
                "ax2",
                EMF_Creator.Strategy.CREATE);
        facade = UserFacade.getUserFacade(emf);
    }

    @BeforeAll
    public static void setUpClassV2() {
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.DROP_AND_CREATE);
        facade = UserFacade.getUserFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("users.deleteAllRows").executeUpdate();
            user1 = new User("kim", "Password123", true);
            user2 = new User("larsen", "VerySecureP4ssword");
            em.persist(user1);
            em.persist(user2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        users = new User[]{user1, user2};
    }

    @AfterEach
    public void tearDown() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("users.deleteAllRows").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    @Test
    public void testDeleteUser() {
        facade.deleteUser(user1.getUserName());

        EntityManager em = emf.createEntityManager();
        try {
            List<User> dbResult = em.createQuery("Select u FROM User u", User.class).getResultList();
            assertEquals(dbResult.size(), users.length - 1);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            em.close();
        }
    }
}