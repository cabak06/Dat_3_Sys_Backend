package facades;

import dto.InternalMemeDTO;
import dto.InternalMemesDTO;
import entities.InternalMeme;
import entities.User;
import java.util.Date;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

public class InternalMemeFacade {

    private static InternalMemeFacade instance;
    private static EntityManagerFactory emf;
    private UserFacade uf;

    private InternalMemeFacade() {
        this.uf = UserFacade.getUserFacade(emf);
    }

    public static InternalMemeFacade getMemeFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new InternalMemeFacade();
        }
        return instance;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public long getInternalMemeCount() {
        EntityManager em = emf.createEntityManager();
        try {
            long internalMemeCount = (long) em.createQuery("SELECT COUNT(i) FROM InternalMeme i").getSingleResult();
            return internalMemeCount;
        } finally {
            em.close();
        }
    }
    
    public InternalMemesDTO getUserMemes(String user) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<InternalMeme> query;
            User dbUser = em.find(User.class, user);
            boolean nsfw = dbUser.isNsfwIsActive();
            if (nsfw) {
                query = em.createQuery("SELECT i FROM InternalMeme i", InternalMeme.class);
            } else {
                query = em.createQuery("SELECT i FROM InternalMeme i WHERE i.nsfw = false", InternalMeme.class);
            }
            List<InternalMeme> dbList = query.getResultList();
            InternalMemesDTO result = new InternalMemesDTO(dbList);
            return result;
        } finally {
            em.close();
        }
    }
    
    public InternalMemeDTO addMeme(InternalMemeDTO meme) {
        User user = uf.getUser(meme.getCreatedBy());
        EntityManager em = emf.createEntityManager();

        try {
            InternalMeme mj = new InternalMeme(user, meme.getPicturePath(), meme.getTitle(), meme.isNsfw());
            em.getTransaction().begin();
            em.persist(mj);
            em.getTransaction().commit();
            InternalMemeDTO newMeme = new InternalMemeDTO(mj);
            return newMeme;
        } finally {
            em.close();
        }
    }
}