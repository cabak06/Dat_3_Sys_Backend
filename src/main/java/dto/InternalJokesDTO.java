
package dto;

import entities.InternalJoke;
import java.util.ArrayList;
import java.util.List;


public class InternalJokesDTO {
   
     private List<InternalJokeDTO> jokeList = new ArrayList();
    
    public InternalJokesDTO(){
    }

    public InternalJokesDTO(List<InternalJoke> jokes) {
        for (InternalJoke joke : jokes) {
            this.jokeList.add(new InternalJokeDTO(joke));
        }
    }

    public List<InternalJokeDTO> getJokes() {
        return jokeList;
    }

    public void setJokes(List<InternalJokeDTO> JokesList) {
        this.jokeList = jokeList;
    }
}
    
    
    

