package facades;

import dto.UserDTO;
import dto.UsersDTO;
import entities.Role;
import entities.User;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import errorhandling.AuthenticationException;
import errorhandling.InvalidInputException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.persistence.TypedQuery;

public class UserFacade {

    private static EntityManagerFactory emf;
    private static UserFacade instance;

    private UserFacade() {
    }

    public static UserFacade getUserFacade(EntityManagerFactory _emf) {
        if (instance == null) {
            emf = _emf;
            instance = new UserFacade();
        }
        return instance;
    }

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

    public User getSpecificUser(String username) {
        EntityManager em = emf.createEntityManager();
        User user;
        try {
            user = em.find(User.class, username);
        } finally {
            em.close();
        }
        return user;
    }
    
    public UsersDTO getUsers() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
            Role admin = em.find(Role.class, "admin");
            List<User> dbList = query.getResultList();
            UsersDTO result = new UsersDTO(dbList);
            return result;
        } finally {
            em.close();
        }
    }
    
    public UsersDTO getNonAdminUsers() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
            Role admin = em.find(Role.class, "admin");
            List<User> dbList = query.getResultList();
            List<User> resultList = new ArrayList();
            for (User user : dbList) {
                if(!user.getRoleList().contains(admin)){
                    resultList.add(user);
                }
            }
            UsersDTO result = new UsersDTO(resultList);
            return result;
        } finally {
            em.close();
        }
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

        //Check for password criteria
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
            if (!isWithinLengthRange || !hasUppercase || !hasLowercase || !hasNumber) {
                return "Password in invalid. Need 1 upper- and lower-case, one number, "
                        + "and needs to be between 5 and 20 characters.";
            }
        } else {
            return "No password given";
        }

        //Create user
        em = emf.createEntityManager();
        try {
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

    public UserDTO updateUser(UserDTO user) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User dbUser = em.find(User.class, user.getUsername());
            dbUser.setNsfwIsActive(user.isNsfwActive());
            em.getTransaction().commit();
            return new UserDTO(dbUser);
        } finally {
            em.close();
        }
    }

    public UserDTO updateUserPassword(UserDTO user) throws InvalidInputException {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User dbUser = em.find(User.class, user.getUsername());
            boolean correctPassword = dbUser.verifyPassword(user.getPassword());
            if (!correctPassword) {
                throw new InvalidInputException("Old password is incorrect!");
            }
            String password = user.getNewPassword();

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
            if (isWithinLengthRange && hasUppercase && hasLowercase && hasNumber) {
                dbUser.setUserPass(user.getNewPassword());
                em.getTransaction().commit();
                return new UserDTO(dbUser);
            } else {
                throw new InvalidInputException("Password is invalid. Need 1 upper-case letter, 1 lower-case letter, at least one number "
                        + "and needs to be between 5 and 20 characters.");
            }

        } finally {
            em.close();
        }
    }
    
    public void deleteUser(String userName) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User u = em.find(User.class, userName);
            em.remove(u);
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }
}