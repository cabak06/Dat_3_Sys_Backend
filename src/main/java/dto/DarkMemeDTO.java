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
public class DarkMemeDTO {
    
    private final static String RANDOM_URL = "https://meme-api.glitch.me/moderate";
    private String meme;

    public DarkMemeDTO() {
    }
    
    public static String getRANDOM_URL() {
        return RANDOM_URL;
    }
    
    public String getMeme() {
        return meme;
    }

    public void setMeme(String meme) {
        this.meme = meme;
    }
    
    

    

}
