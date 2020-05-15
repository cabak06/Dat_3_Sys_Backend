package dto;

import entities.ExternalJoke;
import java.util.ArrayList;
import java.util.List;

public class ExternalJokesDTO {
    
    private List<ExternalJokeDTO> ExternalJokeList = new ArrayList();
    
    public ExternalJokesDTO(){
    }

    public ExternalJokesDTO(List<ExternalJoke> jokes) {
        for (ExternalJoke joke : jokes) {
            this.ExternalJokeList.add(new ExternalJokeDTO(joke));
        }
    }

    public List<ExternalJokeDTO> getExternalJokes() {
        return ExternalJokeList;
    }

    public void setJokes(List<ExternalJokeDTO> ExternalJokesList) {
        this.ExternalJokeList = ExternalJokeList;
    }
}