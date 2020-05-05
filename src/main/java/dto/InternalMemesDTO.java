package dto;

import entities.InternalMeme;
import java.util.ArrayList;
import java.util.List;


public class InternalMemesDTO {
   
     private List<InternalMemeDTO> memeList = new ArrayList();
    
    public InternalMemesDTO(){
    }

    public InternalMemesDTO(List<InternalMeme> memes) {
        for (InternalMeme meme : memes) {
            this.memeList.add(new InternalMemeDTO(meme));
        }
    }

    public List<InternalMemeDTO> getMemes() {
        return memeList;
    }

    public void setMemes(List<InternalMemeDTO> MemeList) {
        this.memeList = memeList;
    }
}