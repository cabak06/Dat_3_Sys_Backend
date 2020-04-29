package facades;

import dto.UserDTO;
import entities.Role;
import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import errorhandling.AuthenticationException;
import java.util.Objects;

/**
 * @author lam@cphbusiness.dk
 */
public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private UserFacade() {
    }

    /**
     *
     * @param _emf
     * @return the instance of this facade.
     */
    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

    /* used to search for a user and throws an exception if user either doesn't
    exist or the values are wrong */
    public User getVeryfiedUser(String username, String password) throws AuthenticationException {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
            if (user == null || !user.verifyPassword(password)) {
                throw new AuthenticationException("Invalid user name or password");
            }
        } finally {
            em.close();
        }
        return user;
    }

    public User getUser(String username) {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
        } finally {
            em.close();
        }
        return user;
    }

    public String createUser(UserDTO newUser) {

        //Check for dublicate username
        EntityManager em = emf.createEntityManager();
        try {
            User dbUser = em.find(User.class, newUser.getUsername());
            if (Objects.nonNull(dbUser)) {
                return "Dublicate Username";
            }
        } finally {
            em.close();
        }

        //Check for password critiria
        String password = newUser.getPassword();
        if (Objects.nonNull(password)) {
            boolean isWithinLengthRange = password.length() > 4 && password.length() < 21;
            boolean hasUppercase = !password.equals(password.toLowerCase());
            boolean hasLowercase = !password.equals(password.toUpperCase());
            boolean hasNumber = false;
            for (char letter : password.toCharArray()) {
                if (Character.isDigit(letter)) {
                    hasNumber = true;
                    break;
                }
            }
            if(!isWithinLengthRange || !hasUppercase || !hasLowercase || !hasNumber){
                return "Password in invalid. Need 1 upper- and lower-case, one number, "
                        + "and needs to be between 5 and 20 characters.";
            }
        } else {
            return "No password given";
        }

        //Create user
        em = emf.createEntityManager();
        try{
            em.getTransaction().begin();
            Role userRole = em.find(Role.class, "user");
            User user = new User(newUser.getUsername(), newUser.getPassword());
            user.addRole(userRole);
            em.persist(user);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
        
        return "";
    }

}
