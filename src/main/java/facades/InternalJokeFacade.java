package facades;

import dto.InternalJokeDTO;
import dto.InternalJokesDTO;
import entities.InternalJoke;
import entities.User;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;

public class InternalJokeFacade {

    private static InternalJokeFacade instance;
    private static EntityManagerFactory emf;
    private UserFacade uf;
    
    private InternalJokeFacade() { 
        this.uf = UserFacade.getUserFacade(emf);
    }
    
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
    
    //returning all jokes created by User X
    public InternalJokesDTO getUserJokesForSpecificUser(String createdBy) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<InternalJoke> query = em.createQuery("SELECT i FROM InternalJoke i WHERE i.createdBy = :createdBy", InternalJoke.class)
                .setParameter("createdBy", createdBy);
            List<InternalJoke> dbList = query.getResultList();
            InternalJokesDTO result = new InternalJokesDTO(dbList);
            return result;
        } finally {
            em.close();
        }
    }
    
    public void deleteUserJoke(long id) {
        EntityManager em = emf.createEntityManager();
        try{
            InternalJoke ij = em.find(InternalJoke.class, id);
            em.getTransaction().begin();
            em.remove(ij);
            em.getTransaction().commit();
        }finally{  
            em.close();
        }
    }
    
    public void editUserJoke(long id, InternalJokeDTO internalJokeDTO) {
        EntityManager em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            InternalJoke ij = em.createQuery("SELECT i FROM InternalJoke i WHERE i.id = :id", InternalJoke.class)
                    .setParameter("id", id)
                    .getSingleResult();
            ij.setJokeContent(internalJokeDTO.getJokeContent());
            Date now = new Date();
            ij.setLastEdited(now);
            em.getTransaction().commit();
        }finally{  
            em.close();
        }
    }
}