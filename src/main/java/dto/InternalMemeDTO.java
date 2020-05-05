package dto;
import entities.InternalMeme;

public class InternalMemeDTO {
    private Long id;
    private String createdBy;
    private String picturePath;
    private String title;
    private boolean nsfw;

    public InternalMemeDTO() {
    }

    public InternalMemeDTO(Long id, String createdBy, String jokeContent, String title) {
        this.id = id;
        this.createdBy = createdBy;
        this.picturePath = jokeContent;
        this.title = title;
    }

    public InternalMemeDTO(Long id, String createdBy, String jokeContent, String title, boolean nsfw) {
        this.id = id;
        this.createdBy = createdBy;
        this.picturePath = jokeContent;
        this.title = title;
        this.nsfw = nsfw;
    }
    
    public InternalMemeDTO(String createdBy, String picturePath, String title) {
        this.createdBy = createdBy;
        this.picturePath = picturePath;
        this.title = title;
    }

    public InternalMemeDTO(String createdBy, String picturePath) {
        this.createdBy = createdBy;
        this.picturePath = picturePath;
    }

    public InternalMemeDTO(String picturePath) {
        this.picturePath = picturePath;
    }

    public InternalMemeDTO(InternalMeme meme) {
        this.id = meme.getId();
        this.createdBy = meme.getCreatedBy().getUserName();
        this.picturePath = meme.getPicturePath();
        this.title = meme.getTitle();
        this.nsfw = meme.isNsfw();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
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

    public boolean isNsfw() {
        return nsfw;
    }

    public void setNsfw(boolean nsfw) {
        this.nsfw = nsfw;
    }
}