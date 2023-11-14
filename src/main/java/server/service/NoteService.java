package server.service;
import server.domain.Note;
import server.dto.NoteDTO;
import java.util.List;

public interface NoteService {
    Note createNote(NoteDTO note, String header);
    void editNote(String authorizationHeader, NoteDTO noteDTO);
    void archiveNote(String authorizationHeader, NoteDTO noteDTO);
    void deleteNote(String authorizationHeader, NoteDTO noteDTO);
    List<Note> getNotes(boolean status, String Date,boolean isAllNotes, String header);
}
