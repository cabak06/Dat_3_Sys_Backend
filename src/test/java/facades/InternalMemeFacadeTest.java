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

public class InternalMemeFacadeTest {

    private static EntityManagerFactory emf;
    private static InternalMemeFacade facade;
    private static Long highestId;
    private static InternalMeme meme1, meme2;
    private static InternalMeme[] memes;
    private static User user1, user2;

    public InternalMemeFacadeTest() {
    }

    //@BeforeAll
    public static void setUpClass() {
        emf = EMF_Creator.createEntityManagerFactory(
                "pu",
                "jdbc:mysql://localhost:3307/startcode_test",
                "dev",
                "ax2",
                EMF_Creator.Strategy.CREATE);
        facade = InternalMemeFacade.getMemeFacade(emf);
    }

    @BeforeAll
    public static void setUpClassV2() {
        emf = EMF_Creator.createEntityManagerFactory(DbSelector.TEST, Strategy.DROP_AND_CREATE);
        facade = InternalMemeFacade.getMemeFacade(emf);
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("InternalMeme.deleteAllRows").executeUpdate();
            user1 = new User("kim", "Password123", true);
            user2 = new User("larsen", "VerySecureP4ssword");
            meme1 = new InternalMeme(user1, "Haha.com", "much fun");
            meme2 = new InternalMeme(user1, "lolollmao.dk", "rofl", true);
            em.persist(user1);
            em.persist(user2);
            em.persist(meme1);
            em.persist(meme2);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        memes = new InternalMeme[]{meme1, meme2};
        highestId = 0L;
        for (InternalMeme meme : memes) {
            if (meme.getId() > highestId) {
                highestId = meme.getId();
            }
        }
    }

    @AfterEach
    public void tearDown() {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            em.createNamedQuery("InternalMeme.deleteAllRows").executeUpdate();
            em.createQuery("DELETE FROM User").executeUpdate();

            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    // TODO: Delete or change this method 
    @Test
    public void testMemeCount() {
        assertEquals(2, facade.getInternalMemeCount(), "Expects two rows in the database");
    }

    @Test
    public void testGetMemes_nsfwActive() {
        User user = user1;
        if (!user.isNsfwIsActive()) {
            fail("User for test did not have NSFW active");
        }
        InternalMemesDTO result = facade.getUserMemes(user.getUserName());

        int expectedSize = memes.length;
        assertEquals(expectedSize, result.getMemes().size());
    }

    @Test
    public void testGetMemes_nsfwNotActive() {
        User user = user2;
        if (user.isNsfwIsActive()) {
            fail("User for test did have NSFW active");
        }
        int expectedSize = 0;
        for (InternalMeme meme : memes) {
            if (meme.isNsfw()) {
                expectedSize++;
            }
        }

        InternalMemesDTO result = facade.getUserMemes(user.getUserName());

        assertEquals(expectedSize, result.getMemes().size());
        for (InternalMemeDTO meme : result.getMemes()) {
            assertFalse(meme.isNsfw());
        }
    }
    
    @Test
    public void testAddMeme() {
        InternalMemeDTO memeDTO = new InternalMemeDTO(user1.getUserName(), "Funny stuff", "haha");
        Long expectedId = highestId + 1;

        InternalMemeDTO result = facade.addMeme(memeDTO);

        assertTrue(result.getCreatedBy().equals(memeDTO.getCreatedBy()));
        assertTrue(result.getPicturePath().equals(memeDTO.getPicturePath()));
        assertTrue(result.getTitle().equals(memeDTO.getTitle()));
        assertEquals(expectedId, result.getId());
    }
    
    @Test
    public void testDeleteMeme() {
        facade.deleteUserMeme(meme1.getId());

        EntityManager em = emf.createEntityManager();
        try {
            List<InternalMeme> dbResult = em.createQuery("Select m FROM InternalMeme m", InternalMeme.class).getResultList();
            assertEquals(memes.length - 1, dbResult.size());
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            em.close();
        }
    }
}