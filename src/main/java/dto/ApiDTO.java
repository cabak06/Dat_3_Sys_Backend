package dto;

import java.util.Objects;

public class ApiDTO {

    private String chuckJoke;
    private String chuckJokeID;
    private final String chuckURL = ChuckJokeDTO.getRANDOM_URL();
    private String dadJoke;
    private String dadJokeID;
    private final String dadURL = DadJokeDTO.getRANDOM_URL();
    private String countryName;
    private String countryArea;
    private final String countryURL = CountryDTO.getCOUNTRY_URL();
    private String copenhagenTime;
    private String weekNumber;
    private final String timeURL = CopenhagenTimeDTO.getTIME_URL();
    private final static String COULD_NOT_FETCH = "Could not fetch this data";

    public ApiDTO() {
    }

    public ApiDTO(ChuckJokeDTO chuck, DadJokeDTO dad, CountryDTO country, CopenhagenTimeDTO time) {
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
        if (!Objects.isNull(country)) {
            this.countryName = country.getName();
            this.countryArea = country.getArea();
        } else {
            this.countryName = COULD_NOT_FETCH;
            this.countryArea = COULD_NOT_FETCH;
        }
        if (!Objects.isNull(time)) {
            this.copenhagenTime = time.getDatetime();
            this.weekNumber = time.getWeek_number();
        } else {
            this.copenhagenTime = COULD_NOT_FETCH;
            this.weekNumber = COULD_NOT_FETCH;
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

    public String getCountryName() {
        return countryName;
    }

    public String getCountryArea() {
        return countryArea;
    }

    public String getCountryURL() {
        return countryURL;
    }

    public void setCountryName(String countryName) {
        this.countryName = countryName;
    }

    public void setCountryArea(String countryArea) {
        this.countryArea = countryArea;
    }

    public String getCopenhagenTime() {
        return copenhagenTime;
    }

    public void setCopenhagenTime(String copenhagenTime) {
        this.copenhagenTime = copenhagenTime;
    }

    public String getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(String weekNumber) {
        this.weekNumber = weekNumber;
    }
}