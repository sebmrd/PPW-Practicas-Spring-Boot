package ec.edu.ups.icc.fundamentos01.users.repository;

import java.util.List;

import org.apache.catalina.User;

public class UserRepository {
    private List<User> users;

    public UserRepository(List<User> users) {
        this.users = users;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
