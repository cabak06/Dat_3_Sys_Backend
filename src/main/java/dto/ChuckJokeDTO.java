package dto;

public class ChuckJokeDTO {
    
    private final static String RANDOM_URL = "https://api.chucknorris.io/jokes/random";
    private String value;
    private String id;

    public ChuckJokeDTO() {
    }

    public ChuckJokeDTO(String value, String id) {
        this.value = value;
        this.id = id;
    }

    public static String getRANDOM_URL() {
        return RANDOM_URL;
    }

    public String getValue() {
        return value;
    }

    public String getId() {
        return id;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setId(String id) {
        this.id = id;
    }
    
    
    
}
