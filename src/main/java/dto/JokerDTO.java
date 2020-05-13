package dto;

public class JokerDTO {

    private final static String RANDOM_URL = "https://www.jokerthewizard.dk/jokesapp/jokes/random";
    private String category;
    private String joke;
    private Long id;
    private boolean nsfw;

    public JokerDTO(String category, String joke, Long id, boolean nsfw) {
        this.category = category;
        this.joke = joke;
        this.id = id;
        this.nsfw = nsfw;
    }

    public JokerDTO() {
    }

    public void fixJoke() {
        String fixedJoke = joke.replaceAll("â€™", "'");
        fixedJoke = fixedJoke.replaceAll("â€œ", "\"");
        setJoke(fixedJoke);
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getJoke() {
        return joke;
    }

    public void setJoke(String joke) {
        this.joke = joke;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isNsfw() {
        return nsfw;
    }

    public void setNsfw(boolean nsfw) {
        this.nsfw = nsfw;
    }

    public static String getRANDOM_URL() {
        return RANDOM_URL;
    }

}
