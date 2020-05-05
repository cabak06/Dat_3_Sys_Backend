/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

/**
 *
 * @author andre
 */
public class RedditMemeDTO {
    
    private final static String RANDOM_URL = "https://meme-api.herokuapp.com/gimme";
    private String url;
    private String title;
    private String postLink;

    public RedditMemeDTO() {
    }

    public RedditMemeDTO(String url, String title, String postLink) {
        this.url = url;
        this.title = title;
        this.postLink = postLink;
    }
    
    public static String getRANDOM_URL() {
        return RANDOM_URL;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPostLink() {
        return postLink;
    }

    public void setPostLink(String postLink) {
        this.postLink = postLink;
    }

    

}
