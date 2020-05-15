package facades;

import dto.InternalJokeDTO;
import dto.InternalJokesDTO;
import entities.InternalJoke;
import entities.User;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

public class InternalJokeFacade {

    private static InternalJokeFacade instance;
    private static EntityManagerFactory emf;
    private UserFacade uf;

    private InternalJokeFacade() {
        this.uf = UserFacade.getUserFacade(emf);
    }

    public static InternalJokeFacade getJokeFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new InternalJokeFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    //TODO Remove/Change this before use
    public long getInternalJokeCount() {
        EntityManager em = emf.createEntityManager();
        try {
            long internalJokeCount = (long) em.createQuery("SELECT COUNT(i) FROM InternalJoke i").getSingleResult();
            return internalJokeCount;
        } finally {
            em.close();
        }
    }

    public InternalJokeDTO addJoke(InternalJokeDTO joke) {
        User user = uf.getSpecificUser(joke.getCreatedBy());
        EntityManager em = emf.createEntityManager();

        try {
            InternalJoke ij = new InternalJoke(user, joke.getJokeContent(), joke.isNsfw());
            em.getTransaction().begin();
            em.persist(ij);
            em.getTransaction().commit();
            InternalJokeDTO newJoke = new InternalJokeDTO(ij);
            return newJoke;
        } finally {
            em.close();
        }
    }

    public InternalJokesDTO getUserJokes(String user) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<InternalJoke> query;
            User dbUser = em.find(User.class, user);
            boolean nsfw = dbUser.isNsfwIsActive();
            if (nsfw) {
                query = em.createQuery("SELECT i FROM InternalJoke i", InternalJoke.class);
            } else {
                query = em.createQuery("SELECT i FROM InternalJoke i WHERE i.nsfw = false", InternalJoke.class);
            }
            List<InternalJoke> dbList = query.getResultList();
            InternalJokesDTO result = new InternalJokesDTO(dbList);
            return result;
        } finally {
            em.close();
        }
    }
    
    //returning all jokes created by User X
    public InternalJokesDTO getUserJokesForSpecificUser(String userName) {
        EntityManager em = emf.createEntityManager();
        try {
            User user = em.find(User.class, userName);
            TypedQuery<InternalJoke> query = em.createQuery("SELECT i FROM InternalJoke i WHERE i.createdBy = :userName", InternalJoke.class)
                .setParameter("userName", user);
            List<InternalJoke> dbList = query.getResultList();
            InternalJokesDTO result = new InternalJokesDTO(dbList);
            return result;
        } finally {
            em.close();
        }
    }
    
    public void deleteUserJokeAsAdmin(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            InternalJoke ij = em.find(InternalJoke.class, id);
            em.getTransaction().begin();
            em.remove(ij);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    public void deleteUserJokeAsUser(String userName, Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            User user = em.find(User.class, userName);
            InternalJoke ij = em.createQuery("SELECT i FROM InternalJoke i WHERE i.createdBy = :userName AND i.id = :id", InternalJoke.class)
                .setParameter("userName", user)
                .setParameter("id", id)
                .getSingleResult();
            em.getTransaction().begin();
            em.remove(ij);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
    
    public InternalJokeDTO editUserJoke(String userName, InternalJokeDTO joke) {
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            User user = em.find(User.class, userName);
            InternalJoke ij = em.find(InternalJoke.class, joke.getId());
            ij.setJokeContent(joke.getJokeContent());
            ij.setNsfw(joke.isNsfw());
            Date now = new Date();
            ij.setLastEdited(now);
            em.getTransaction().commit();
            InternalJokeDTO result = new InternalJokeDTO(ij);
            return result;
        }finally{  
            em.close();
        }
    }


    public InternalJokeDTO addJokeToFavoriteList(String username, Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User user = em.find(User.class,username);
            InternalJoke favorite = em.find(InternalJoke.class, id);
            if(!user.getFavoriteJokes().contains(favorite)){
            user.getFavoriteJokes().add(favorite);    
            }
            em.getTransaction().commit();
            InternalJokeDTO newJoke = new InternalJokeDTO(favorite);
            return newJoke;
        } finally {
            em.close();
        }
    }

    public InternalJokesDTO getUserFavorites(String username) {
        User user = uf.getSpecificUser(username);
        InternalJokesDTO results = new InternalJokesDTO(user.getFavoriteJokes());
        return results;
    }

    
public InternalJokeDTO removeJokeFromFavoriteList(String username, Long id) {
       
        EntityManager em = emf.createEntityManager();

        try {
            
            em.getTransaction().begin();
            User user = em.find(User.class,username);
            InternalJoke favorite = em.find(InternalJoke.class, id);
            if(user.getFavoriteJokes().contains(favorite)){
            user.getFavoriteJokes().remove(favorite);    
            }
            em.getTransaction().commit();
            InternalJokeDTO newJoke = new InternalJokeDTO(favorite);
            return newJoke;
        } finally {
            em.close();
        }
    }

}