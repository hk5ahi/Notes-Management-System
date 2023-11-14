package server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.domain.Note;
import server.dto.NoteDTO;
import server.service.NoteService;

import java.util.List;

@RestController
@RequestMapping("/notes")
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @PostMapping()
    public ResponseEntity<Note> createNote(@RequestBody NoteDTO noteDTO, @RequestHeader("Authorization") String authorizationHeader) {
        Note note = noteService.createNote(noteDTO, authorizationHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(note);
    }

    @GetMapping()
    public ResponseEntity<List<Note>> getNotes(
            @RequestParam(name = "isArchive", required = false, defaultValue = "false") boolean status,
            @RequestParam(name = "Date", required = false, defaultValue = "null") String Date,
            @RequestParam(value = "isAllNotes", defaultValue = "false") boolean isAllNotes,
            @RequestHeader("Authorization") String authorizationHeader
    ) {
        List<Note> notes = noteService.getNotes(status, Date, isAllNotes, authorizationHeader);
        return ResponseEntity.ok(notes);
    }

    @PutMapping("/edit")
    public ResponseEntity<String> edit(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody NoteDTO noteDTO
    ) {
        noteService.editNote(authorizationHeader, noteDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/delete")
    public ResponseEntity<String> delete(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody NoteDTO noteDTO
    ) {
        noteService.deleteNote(authorizationHeader, noteDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PutMapping("/archive")
    public ResponseEntity<String> archive(
            @RequestHeader("Authorization") String authorizationHeader,
            @RequestBody NoteDTO noteDTO
    ) {
        noteService.archiveNote(authorizationHeader, noteDTO);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
