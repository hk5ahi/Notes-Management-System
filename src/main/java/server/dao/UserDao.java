package server.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import server.domain.User;
import java.util.Optional;

public interface UserDao extends JpaRepository<User, String> {
    boolean existsByUsername(String Username);
    Optional<User> getUserByUsernameAndPassword(String Username, String Password);

}
