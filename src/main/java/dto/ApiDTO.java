package dto;

import java.util.Objects;

public class ApiDTO {

    private String chuckJoke;
    private String chuckJokeID;
    private final String chuckURL = ChuckJokeDTO.getRANDOM_URL();
    private String dadJoke;
    private String dadJokeID;
    private final String dadURL = DadJokeDTO.getRANDOM_URL();
    private final static String COULD_NOT_FETCH = "Could not fetch this data";

    public ApiDTO() {
    }

    public ApiDTO(ChuckJokeDTO chuck, DadJokeDTO dad) {
        if (!Objects.isNull(chuck)) {
            this.chuckJoke = chuck.getValue();
            this.chuckJokeID = chuck.getId();
        } else {
            this.chuckJoke = COULD_NOT_FETCH;
            this.chuckJokeID = COULD_NOT_FETCH;
        }
        if (!Objects.isNull(dad)) {
            this.dadJoke = dad.getJoke();
            this.dadJokeID = dad.getId();
        } else {
            this.dadJoke = COULD_NOT_FETCH;
            this.dadJokeID = COULD_NOT_FETCH;
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