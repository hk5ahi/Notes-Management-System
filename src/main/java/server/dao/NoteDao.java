package server.dao;
import org.springframework.data.jpa.repository.JpaRepository;
import server.domain.Note;
import java.util.List;
import java.util.Optional;

public interface NoteDao extends JpaRepository<Note, String> {
    Optional<Note> findByNoteId(Long id);
    Optional<Note> findByNoteIdAndIsDeleteIsFalse(Long id);
    Optional<Note> findByTitleAndIsDeleteIsFalse(String title);
    List<Note> findAllByIsArchiveIsTrueAndIsDeleteIsFalse();
    List<Note> findAllByIsArchiveIsFalseAndIsDeleteIsFalse();
    List<Note> findAllByIsDeleteIsFalse();

}
