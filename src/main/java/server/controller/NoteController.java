package server.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.dto.NoteDTO;
import server.service.NoteService;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping()
    public ResponseEntity<String> createNote(@RequestBody NoteDTO noteDTO, @RequestHeader("Authorization") String authorizationHeader) {
        noteService.createNote(noteDTO, authorizationHeader);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping()
    public ResponseEntity<String> update(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody NoteDTO noteDTO,
            @RequestParam(value = "isArchive", defaultValue = "false") boolean isArchive,
            @RequestParam(value = "isDelete", defaultValue = "false") boolean isDelete) {
        noteDTO.setArchive(isArchive);
        noteDTO.setDelete(isDelete);
        noteService.updateNote(authorizationHeader, noteDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

}
