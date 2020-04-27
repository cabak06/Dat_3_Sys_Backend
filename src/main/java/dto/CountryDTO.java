package dto;

public class CountryDTO {
    
    private final static String COUNTRY_URL = "https://restcountries.eu/rest/v2/capital/copenhagen";
    private String name;
    private String capital;
    private String region;
    private String area;

    public CountryDTO() {
    }

    public CountryDTO(String name, String capital, String region, String area) {
        this.name = name;
        this.capital = capital;
        this.region = region;
        this.area = area;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCapital() {
        return capital;
    }

    public void setCapital(String capital) {
        this.capital = capital;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public static String getCOUNTRY_URL() {
        return COUNTRY_URL;
    }
    
}
