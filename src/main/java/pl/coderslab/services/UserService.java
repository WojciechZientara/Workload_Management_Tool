package pl.coderslab.services;
import pl.coderslab.entities.User;

public interface UserService {

    User findByUserEmail(String email);
    void saveUser(User user);
}
