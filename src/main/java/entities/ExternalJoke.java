package entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "ExternalJoke.deleteAllRows", query = "DELETE from ExternalJoke")
public class ExternalJoke implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String jokeContent;
    private boolean nsfw;
    
    @Column(unique=true)
    private String jokeAddress;
    
    @ManyToMany(mappedBy = "favoriteExternalJokes", cascade = CascadeType.DETACH)
    private List<User> favoriteExternalJokeUsers = new ArrayList();

    public ExternalJoke() {
    }
    
    public ExternalJoke(String jokeContent, boolean nsfw, String jokeAddress) {
        this.jokeContent = jokeContent;
        this.nsfw = nsfw;
        this.jokeAddress = jokeAddress;
    }

    public ExternalJoke(Long id, String jokeContent, boolean nsfw, String jokeAddress) {
        this.id = id;
        this.jokeContent = jokeContent;
        this.nsfw = nsfw;
        this.jokeAddress = jokeAddress;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJokeContent() {
        return jokeContent;
    }

    public void setJokeContent(String jokeContent) {
        this.jokeContent = jokeContent;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    public void setNsfw(boolean nsfw) {
        this.nsfw = nsfw;
    }

    public String getJokeAddress() {
        return jokeAddress;
    }

    public void setJokeAddress(String jokeAddress) {
        this.jokeAddress = jokeAddress;
    }

    public List<User> getFavoriteExternalJokeUsers() {
        return favoriteExternalJokeUsers;
    }

    public void setFavoriteExternalJokeUsers(List<User> favoriteExternalJokeUsers) {
        this.favoriteExternalJokeUsers = favoriteExternalJokeUsers;
    }
}