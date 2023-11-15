package server.service;
import server.domain.User;
import java.util.List;

public interface UserService {
    void initializeUsers(List<User> users);
}
