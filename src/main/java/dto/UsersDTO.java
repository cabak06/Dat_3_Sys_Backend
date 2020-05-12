package dto;

import entities.InternalJoke;
import entities.User;
import java.util.ArrayList;
import java.util.List;


public class UsersDTO {
   
    private List<UserDTO> userList = new ArrayList();
    
    public UsersDTO(){
    }

    public UsersDTO(List<User> users) {
        for (User user : users) {
            this.userList.add(new UserDTO(user));
        }
    }

    public List<UserDTO> getUsers() {
        return userList;
    }

    public void setUsers(List<UserDTO> UsersList) {
        this.userList = userList;
    }
}