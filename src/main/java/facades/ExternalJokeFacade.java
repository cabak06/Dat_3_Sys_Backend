package facades;

import dto.ExternalJokeDTO;
import dto.ExternalJokesDTO;
import entities.ExternalJoke;
import entities.User;
import java.util.Objects;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

public class ExternalJokeFacade {
    
    private static ExternalJokeFacade instance;
    private static EntityManagerFactory emf;
    private UserFacade uf;

    private ExternalJokeFacade() {
        this.uf = UserFacade.getUserFacade(emf);
    }

    public static ExternalJokeFacade getJokeFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new ExternalJokeFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }
    
    public ExternalJokeDTO addExternalJokeToFavoriteList(String username, ExternalJokeDTO joke) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class,username);
            ExternalJoke favorite;
            try {
                favorite = em.createQuery("SELECT e FROM ExternalJoke e WHERE e.jokeAddress = :jokeAddress", ExternalJoke.class)
                    .setParameter("jokeAddress", joke.getJokeAddress())
                    .getSingleResult();
            } catch(NoResultException e) {
                favorite = new ExternalJoke(joke.getId(), joke.getJokeContent(), joke.isNsfw(), joke.getJokeAddress());
                em.persist(favorite);
            }
            if(!user.getFavoriteExternalJokes().contains(favorite)){
                user.getFavoriteExternalJokes().add(favorite);    
            }
            em.getTransaction().commit();
            ExternalJokeDTO newJoke = new ExternalJokeDTO(favorite);
            return newJoke;
        } finally {
            em.close();
        }
    }
    
    public ExternalJokesDTO getUserFavorites(String username) {
        User user = uf.getSpecificUser(username);
        ExternalJokesDTO results = new ExternalJokesDTO(user.getFavoriteExternalJokes());
        return results;
    }
    
    public ExternalJokeDTO removeJokeFromFavoriteList(String username, Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class,username);
            ExternalJoke favorite = em.find(ExternalJoke.class, id);
            if(user.getFavoriteExternalJokes().contains(favorite)){
            user.getFavoriteExternalJokes().remove(favorite);    
            }
            em.getTransaction().commit();
            ExternalJokeDTO newJoke = new ExternalJokeDTO(favorite);
            return newJoke;
        } finally {
            em.close();
        }
    }
}