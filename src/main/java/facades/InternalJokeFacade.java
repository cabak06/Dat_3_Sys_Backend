package facades;

import dto.InternalJokeDTO;
import entities.InternalJoke;
import entities.User;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * Rename Class to a relevant name Add add relevant facade methods
 */
public class InternalJokeFacade {

    private static InternalJokeFacade instance;
    private static EntityManagerFactory emf;
    private UserFacade uf;
    
    //Private Constructor to ensure Singleton
    private InternalJokeFacade() { 
        this.uf = UserFacade.getUserFacade(emf);
    }
    
    
    /**
     * 
     * @param _emf
     * @return an instance of this facade class.
     */
    public static InternalJokeFacade getFacadeExample(EntityManagerFactory _emf) {
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
    public long getInternalJokeCount(){
        EntityManager em = emf.createEntityManager();
        try{
            long internalJokeCount = (long)em.createQuery("SELECT COUNT(i) FROM InternalJoke i").getSingleResult();
            return internalJokeCount;
        }finally{  
            em.close();
        }
        
    }
    
    public InternalJokeDTO addJoke(InternalJokeDTO joke) {
        User user = uf.getUser(joke.getCreatedBy());
        EntityManager em = emf.createEntityManager();
        
        try{
            InternalJoke ij = new InternalJoke(user, joke.getJokeContent());
            em.getTransaction().begin();
            em.persist(ij);
            em.getTransaction().commit();
            InternalJokeDTO newJoke = new InternalJokeDTO(ij);
            return newJoke;
        }finally{  
            em.close();
        }
    }

}
