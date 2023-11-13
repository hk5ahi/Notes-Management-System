package server.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import server.domain.Note;
import java.util.Optional;

public interface NoteDao extends JpaRepository<Note, String> {

    Optional<Note> findByNoteId(Long id);

}
