/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import entities.InternalJoke;
import java.util.Date;

/**
 *
 * @author andre
 */
public class InternalJokeDTO {

    private Long id;
    private String createdBy;
    private String jokeContent;
    private boolean nsfw;
    private Date createdDate;
    
    public InternalJokeDTO() {
    }

    public InternalJokeDTO(Long id, String createdBy, String jokeContent) {
        this.id = id;
        this.createdBy = createdBy;
        this.jokeContent = jokeContent;
      
    }

    public InternalJokeDTO(Long id, String createdBy, String jokeContent, boolean nsfw) {
        this.id = id;
        this.createdBy = createdBy;
        this.jokeContent = jokeContent;
        this.nsfw = nsfw;      
    }
    
    public InternalJokeDTO(String createdBy, String jokeContent) {
        this.createdBy = createdBy;
        this.jokeContent = jokeContent;
    }

    public InternalJokeDTO(String jokeContent) {
        this.jokeContent = jokeContent;
    }

    public InternalJokeDTO(InternalJoke joke) {
        this.id = joke.getId();
        this.createdBy = joke.getCreatedBy().getUserName();
        this.jokeContent = joke.getJokeContent();
        this.nsfw = joke.isNsfw();
        this.createdDate = joke.getCreatedDate();
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
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

}
