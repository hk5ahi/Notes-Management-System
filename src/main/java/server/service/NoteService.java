package server.service;
import server.dto.NoteDTO;
import java.util.List;

public interface NoteService {
    NoteDTO createNote(NoteDTO note, String header);

    NoteDTO editNote(String authorizationHeader, NoteDTO noteDTO);

    void archiveNote(String authorizationHeader, NoteDTO noteDTO);

    void deleteNote(String authorizationHeader, NoteDTO noteDTO);

    List<NoteDTO> getNotes(String status, String Date, String header);

    void validateIfNoteIsNotNull(NoteDTO noteDTO);
}
