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

@Entity
@NamedQuery(name = "InternalMeme.deleteAllRows", query = "DELETE from InternalMeme")
public class InternalMeme implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private User createdBy;
    private String picturePath;
    private String title;

    @Temporal(TemporalType.DATE)
    private Date createdDate;

    @Temporal(TemporalType.DATE)
    private Date lastEdited;

    private boolean nsfw;

    public InternalMeme(User createdBy, String picturePath, String title) {
        this.createdBy = createdBy;
        this.picturePath = picturePath;
        this.title = title;
        this.nsfw = false;
        Date now = new Date();
        this.createdDate = now;
        this.lastEdited = now;
    }

    public InternalMeme(User createdBy, String picturePath, String title, boolean nsfw) {
        this.createdBy = createdBy;
        this.picturePath = picturePath;
        this.title = title;
        this.nsfw = nsfw;
        Date now = new Date();
        this.createdDate = now;
        this.lastEdited = now;
    }

    public InternalMeme() {
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

    public String getPicturePath() {
        return picturePath;
    }

    public void setPicturePath(String picturePath) {
        this.picturePath = picturePath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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