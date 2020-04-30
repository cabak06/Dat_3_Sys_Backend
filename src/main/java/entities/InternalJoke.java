package entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/* This class is intended as a very simple dummy class to turn into another
relevant entity class in the future */
@Entity
@NamedQuery(name = "InternalJoke.deleteAllRows", query = "DELETE from InternalJoke")
public class InternalJoke implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User createdBy;
    private String jokeContent;

    @Temporal(TemporalType.DATE)
    private Date createdDate;

    @Temporal(TemporalType.DATE)
    private Date lastEdited;

    private boolean nsfw;

    public InternalJoke(User createdBy, String jokeContent) {
        this.createdBy = createdBy;
        this.jokeContent = jokeContent;
        this.nsfw = false;
        Date now = new Date();
        this.createdDate = now;
        this.lastEdited = now;
    }

    public InternalJoke(User createdBy, String jokeContent, boolean nsfw) {
        this.createdBy = createdBy;
        this.jokeContent = jokeContent;
        this.nsfw = nsfw;
        Date now = new Date();
        this.createdDate = now;
        this.lastEdited = now;
    }

    public InternalJoke() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getJokeContent() {
        return jokeContent;
    }

    public void setJokeContent(String jokeContent) {
        this.jokeContent = jokeContent;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getLastEdited() {
        return lastEdited;
    }

    public void setLastEdited(Date lastEdited) {
        this.lastEdited = lastEdited;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    public void setNsfw(boolean nsfw) {
        this.nsfw = nsfw;
    }

}
