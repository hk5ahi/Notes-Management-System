package server.service;
import server.dto.NoteDTO;

public interface NoteService {
    void createNote(NoteDTO note, String header);
    void updateNote(String authorizationHeader, NoteDTO taskDTO);
}
