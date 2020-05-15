package dto;

import entities.ExternalJoke;

public class ExternalJokeDTO {
    
    private Long id;
    private String jokeContent;
    private boolean nsfw;
    private String jokeAddress;

    public ExternalJokeDTO() {
    }

    public ExternalJokeDTO(Long id, String jokeContent, boolean nsfw, String jokeAddress) {
        this.id = id;
        this.jokeContent = jokeContent;
        this.nsfw = nsfw;
        this.jokeAddress = jokeAddress;
    }
    
    public ExternalJokeDTO(String jokeContent, boolean nsfw, String jokeAddress) {
        this.jokeContent = jokeContent;
        this.nsfw = nsfw;
        this.jokeAddress = jokeAddress;
    }
    
    public ExternalJokeDTO(ExternalJoke joke) {
        this.id = joke.getId();
        this.jokeContent = joke.getJokeContent();
        this.nsfw = joke.isNsfw();
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
}