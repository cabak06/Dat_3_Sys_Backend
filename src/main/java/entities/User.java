package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.mindrot.jbcrypt.BCrypt;

@Entity
@Table(name = "users")
@NamedQuery(name = "users.deleteAllRows", query = "DELETE from User")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @NotNull
    @Column(name = "user_name", length = 25)
    private String userName;

    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "user_pass")
    private String userPass;

    @JoinTable(name = "user_roles", joinColumns = {
        @JoinColumn(name = "user_name", referencedColumnName = "user_name")}, inverseJoinColumns = {
        @JoinColumn(name = "role_name", referencedColumnName = "role_name")})
    @ManyToMany
    private List<Role> roleList = new ArrayList();

    @OneToMany(mappedBy = "createdBy", cascade = CascadeType.ALL)
    private List<InternalJoke> jokesCreated = new ArrayList();
    
    @OneToMany(cascade = CascadeType.ALL)
    private List<InternalJoke> favoriteJokes = new ArrayList();

    public List<InternalJoke> getFavoriteJokes() {
        return favoriteJokes;
    }

    public void setFavoriteJokes(List<InternalJoke> favoriteJokes) {
        this.favoriteJokes = favoriteJokes;
    }
 
    
    private boolean nsfwIsActive = false;

    public List<String> getRolesAsStrings() {
        if (roleList.isEmpty()) {
            return null;
        }
        List<String> rolesAsStrings = new ArrayList();
        for (Role role : roleList) {
            rolesAsStrings.add(role.getRoleName());
        }
        return rolesAsStrings;
    }

    public User() {
    }

    //TODO Change when password is hashed
    public boolean verifyPassword(String pw) {
        if (BCrypt.checkpw(pw, userPass)) {
            //System.out.println("It matches");   
            return (true);
        } else {
            //System.out.println("It does not match");
            return (false);
        }
    }

    public User(String userName, String userPass) {
        this.userName = userName;
        this.userPass = BCrypt.hashpw(userPass, BCrypt.gensalt());
    }

    public User(String userName, String userPass, boolean nsfwIsActive) {
        this.userName = userName;
        this.userPass = BCrypt.hashpw(userPass, BCrypt.gensalt());
        this.nsfwIsActive = nsfwIsActive;
    }

    public String getUserName() {
        return userName;
    }
    
    

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return this.userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = BCrypt.hashpw(userPass, BCrypt.gensalt());
    }

    public List<Role> getRoleList() {
        return roleList;
    }

    public void setRoleList(List<Role> roleList) {
        this.roleList = roleList;
        for (Role role : roleList) {
            if (role.getRoleName().contains("admin")) {
                this.nsfwIsActive = true;
            }
        }
    }

    public void addRole(Role userRole) {
        roleList.add(userRole);
        if (userRole.getRoleName().contains("admin")) {
            this.nsfwIsActive = true;
        }
    }

    public List<InternalJoke> getJokesCreated() {
        return jokesCreated;
    }

    public void setJokesCreated(List<InternalJoke> jokesCreated) {
        this.jokesCreated = jokesCreated;
    }

    public boolean isNsfwIsActive() {
        return nsfwIsActive;
    }

    public void setNsfwIsActive(boolean nsfwIsActive) {
        this.nsfwIsActive = nsfwIsActive;
    }
}