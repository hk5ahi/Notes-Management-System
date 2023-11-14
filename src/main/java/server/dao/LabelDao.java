package server.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import server.domain.Label;
import java.util.Optional;

public interface LabelDao extends JpaRepository<Label, String> {

    Optional<Label> findByTitle(String title);
}
