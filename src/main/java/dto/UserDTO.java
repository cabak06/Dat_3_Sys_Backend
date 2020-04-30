package dto;

import entities.User;

public class UserDTO {

    private String username;
    private String password;
    private boolean nsfwIsActive;

    public UserDTO() {
    }

    public UserDTO(String username, String password, boolean nsfwIsActive) {
        this.username = username;
        this.password = password;
        this.nsfwIsActive = nsfwIsActive;
    }

    public UserDTO(String username, String password) {
        this.username = username;
        this.password = password;
        this.nsfwIsActive = false;
    }
    
    public UserDTO(User user) {
        this.username = user.getUserName();
        this.password = user.getUserPass();
        this.nsfwIsActive = user.isNsfwIsActive();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isNsfwActive() {
        return nsfwIsActive;
    }

    public void setNsfwIsActive(boolean nsfwIsActive) {
        this.nsfwIsActive = nsfwIsActive;
    }

}
