package dto;

public class DadJokeDTO {
    
    private final static String RANDOM_URL = "https://icanhazdadjoke.com";
    private String joke;
    private String id;

    public DadJokeDTO() {
    }

    public DadJokeDTO(String joke, String id) {
        this.joke = joke;
        this.id = id;
    }

    public String getJoke() {
        return joke;
    }

    public void setJoke(String joke) {
        this.joke = joke;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public static String getRANDOM_URL() {
        return RANDOM_URL;
    }
    
}
