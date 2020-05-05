/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dto;

import java.util.Objects;
import dto.RedditMemeDTO;
import dto.DarkMemeDTO;

/**
 *
 * @author andre
 */
public class AllMemeDTO {

    private String redditMeme;
    private String redditMemeID;
    private String darkMeme;
    private final static String COULD_NOT_FETCH = "Could not fetch this data";

    public AllMemeDTO(RedditMemeDTO redditMeme, DarkMemeDTO darkMeme) {
        if (!Objects.isNull(redditMeme)) {
            this.redditMeme = redditMeme.getUrl();
            this.redditMemeID = redditMeme.getPostLink();
        } else {
            this.redditMeme = COULD_NOT_FETCH;
            this.redditMemeID = COULD_NOT_FETCH;
        }
        if (!Objects.isNull(darkMeme)) {
            this.darkMeme = darkMeme.getMeme();
        } else {
            
            this.darkMeme = COULD_NOT_FETCH;
        }
    }

    public String getRedditMeme() {
        return redditMeme;
    }

    public void setRedditMeme(String redditMeme) {
        this.redditMeme = redditMeme;
    }

    public String getRedditMemeID() {
        return redditMemeID;
    }

    public void setRedditMemeID(String redditMemeID) {
        this.redditMemeID = redditMemeID;
    }

    public String getDarkMeme() {
        return darkMeme;
    }

    public void setDarkMeme(String darkMeme) {
        this.darkMeme = darkMeme;
    }
    
}
