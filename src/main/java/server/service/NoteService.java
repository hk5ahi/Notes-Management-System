package server.service;
import server.dto.NoteDTO;
import java.util.List;

public interface NoteService {
    void createNote(NoteDTO note, String header);
    void updateNote(String authorizationHeader, NoteDTO noteDTO);
    List<NoteDTO> getNotes(boolean status, String Date,boolean isAllNotes, String header);
}
