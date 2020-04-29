package facades;

import dto.InternalJokeDTO;
import dto.InternalJokesDTO;
import entities.InternalJoke;
import entities.User;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

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
    
    public InternalJokesDTO getUserJokes() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<InternalJoke> query = em.createQuery("SELECT i FROM InternalJoke i", InternalJoke.class);
            List<InternalJoke> dbList = query.getResultList();
            InternalJokesDTO result = new InternalJokesDTO(dbList);
            return result;
        } finally {
            em.close();
        }
    }
    
    public InternalJokeDTO deleteUserJoke() {
        EntityManager em = emf.createEntityManager();
        try{
            InternalJokeDTO ij = em.find(InternalJokeDTO.class, 1);
            em.getTransaction().begin();
            em.remove(ij);
            em.getTransaction().commit();
            return ij;
        }finally{  
            em.close();
        }
    }
}