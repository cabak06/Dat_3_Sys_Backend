package dto;

import java.util.Objects;

public class ApiDTO {

    private final static String COULD_NOT_FETCH = "Could not fetch this data";

    private String chuckJoke;
    private String chuckJokeID;
    private final String chuckURL = ChuckJokeDTO.getRANDOM_URL();
    private String dadJoke;
    private String dadJokeID;
    private final String dadURL = DadJokeDTO.getRANDOM_URL();
    private String jokerJoke;
    private String jokerID;
    private boolean jokerNSFW;
    private final String jokerURL = JokerDTO.getRANDOM_URL();

    public ApiDTO() {
    }

    public ApiDTO(ChuckJokeDTO chuck, DadJokeDTO dad, JokerDTO joker) {
        if (!Objects.isNull(chuck)) {
            this.chuckJoke = chuck.getValue();
            this.chuckJokeID = "https://api.chucknorris.io/jokes/" + chuck.getId();
        } else {
            this.chuckJoke = COULD_NOT_FETCH;
            this.chuckJokeID = COULD_NOT_FETCH;
        }
        if (!Objects.isNull(dad)) {
            this.dadJoke = dad.getJoke();
            this.dadJokeID = "https://icanhazdadjoke.com/j/" + dad.getId();
        } else {
            this.dadJoke = COULD_NOT_FETCH;
            this.dadJokeID = COULD_NOT_FETCH;
        }
        if(!Objects.isNull(joker)){
            this.jokerJoke = joker.getJoke();
            this.jokerID = "https://www.jokerthewizard.dk/jokesapp/jokes/" + joker.getId();
            this.jokerNSFW = joker.isNsfw();
        } else {
            this.jokerJoke = COULD_NOT_FETCH;
            this.jokerID = COULD_NOT_FETCH;
            this.jokerNSFW = false;
        }
    }

    public String getChuckJoke() {
        return chuckJoke;
    }

    public void setChuckJoke(String chuckJoke) {
        this.chuckJoke = chuckJoke;
    }

    public String getChuckJokeID() {
        return chuckJokeID;
    }

    public void setChuckJokeID(String chuckJokeID) {
        this.chuckJokeID = chuckJokeID;
    }

    public String getDadJoke() {
        return dadJoke;
    }

    public void setDadJoke(String dadJoke) {
        this.dadJoke = dadJoke;
    }

    public String getDadJokeID() {
        return dadJokeID;
    }

    public void setDadJokeID(String dadJokeID) {
        this.dadJokeID = dadJokeID;
    }

    public String getChuckUrl() {
        return chuckURL;
    }

    public String getDadUrl() {
        return dadURL;
    }

    public String getChuckURL() {
        return chuckURL;
    }

    public String getDadURL() {
        return dadURL;
    }
}
