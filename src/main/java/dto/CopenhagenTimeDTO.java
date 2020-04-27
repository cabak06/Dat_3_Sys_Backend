package dto;

public class CopenhagenTimeDTO {
    
    private final static String TIME_URL = "http://worldtimeapi.org/api/timezone/Europe/Copenhagen";
    private String timezone;
    private String datetime;
    private String day_of_year;
    private String day_of_week;
    private String week_number;

    public CopenhagenTimeDTO() {
    }

    public CopenhagenTimeDTO(String timezone, String datetime, String day_of_year, String day_of_week, String week_number) {
        this.timezone = timezone;
        this.datetime = datetime;
        this.day_of_year = day_of_year;
        this.day_of_week = day_of_week;
        this.week_number = week_number;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getDatetime() {
        return datetime;
    }

    public void setDatetime(String datetime) {
        this.datetime = datetime;
    }

    public String getDay_of_year() {
        return day_of_year;
    }

    public void setDay_of_year(String day_of_year) {
        this.day_of_year = day_of_year;
    }

    public String getDay_of_week() {
        return day_of_week;
    }

    public void setDay_of_week(String day_of_week) {
        this.day_of_week = day_of_week;
    }

    public String getWeek_number() {
        return week_number;
    }

    public void setWeek_number(String week_number) {
        this.week_number = week_number;
    }

    public static String getTIME_URL() {
        return TIME_URL;
    }
    
}
