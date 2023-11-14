package server.service.Implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.dao.NoteDao;
import server.domain.Note;
import server.domain.User;
import server.dto.AuthUserDTO;
import server.dto.NoteDTO;
import server.exception.BadRequestException;
import server.exception.ForbiddenException;
import server.exception.NotFoundException;
import server.service.NoteService;
import server.utilities.UtilityService;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class NoteServiceImpl implements NoteService {

    private final NoteDao noteDao;
    private final UtilityService utilityService;
    private final Logger log = LoggerFactory.getLogger(NoteServiceImpl.class);

    public NoteServiceImpl(NoteDao noteDao, UtilityService utilityService) {
        this.noteDao = noteDao;
        this.utilityService = utilityService;
    }

    @Override
    @Transactional
    public Note createNote(NoteDTO note, String header) {
        User loggedInUser = utilityService.getUser(header);
        Note noteToSave = new Note(note.getTitle(), note.getContent(), note.isArchive(), note.isDelete());
        Instant instant = Instant.now();
        noteToSave.setCreatedAt(instant);
        noteToSave.setCreatedBy(loggedInUser);
        Optional<Note> existedNote = noteDao.findByTitleAndIsDeleteIsFalse(note.getTitle());
        if (existedNote.isPresent() && !existedNote.get().isDelete()) {

            throw new BadRequestException("Invalid Request", "The note already exists with same title" + noteToSave.getTitle());
        } else {
            noteDao.save(noteToSave);
            return noteToSave;
        }
    }

    private void validateIfUserCanArchiveNote(NoteDTO noteDTO, Note existedNote) {
        if (noteDTO.isArchive()) {
            boolean isNoteNeedToArchive = Objects.equals(noteDTO.isArchive(), existedNote.isArchive());

            if (isNoteNeedToArchive) {
                log.error("Already Archived Note");
            }
        }
    }

    private void validateIfUserCanDeleteNote(NoteDTO noteDTO, String authorizationHeader) {
        AuthUserDTO authUserDTO = utilityService.getAuthUser(authorizationHeader);
        if (noteDTO.isDelete() && !authUserDTO.getUserRole().equals(User.userRole.Admin)) {
            throw new ForbiddenException("Invalid Request", "Only Admin can delete the note");
        }
    }

    private Note copyNote(Note prevNote, NoteDTO updateNote) {
        prevNote.setContent(updateNote.getContent());
        prevNote.setTitle(updateNote.getTitle());
        prevNote.setCreatedAt(prevNote.getCreatedAt());
        prevNote.setCreatedBy(prevNote.getCreatedBy());
        return prevNote;
    }

    @Override
    @Transactional
    public void editNote(String authorizationHeader, NoteDTO noteDTO) {
        utilityService.getUser(authorizationHeader);
        Note prevNote = validateIfNoteExists(noteDTO.getNoteId());
        Note newNote = new Note();
        BeanUtils.copyProperties(prevNote, newNote);
        Note updatedNote = copyNote(prevNote, noteDTO);
        noteDao.save(updatedNote);
    }


    @Override
    @Transactional
    public void archiveNote(String authorizationHeader,NoteDTO noteDTO) {
        utilityService.getUser(authorizationHeader);
        for (Long id : noteDTO.getId()) {
            Note prevNote = validateIfNoteExists(id);
            validateIfUserCanArchiveNote(noteDTO, prevNote);
            prevNote.setArchive(true);
            noteDao.save(prevNote);
        }
    }
    @Override
    @Transactional
    public void deleteNote(String authorizationHeader,NoteDTO noteDTO ) {

        for (Long id : noteDTO.getId()) {
            Note prevNote = validateIfNoteExists(id);
            validateIfUserCanDeleteNote(noteDTO, authorizationHeader);
            prevNote.setDelete(true);
            noteDao.save(prevNote);
        }
    }

    private Note validateIfNoteExists(Long id) {
        Note prevNote = noteDao
                .findById(id)
                .orElseThrow(() -> new NotFoundException("Invalid Request", "Note not found"));

        if (prevNote.isDelete()) {
            throw new NotFoundException("Note is deleted", "Note not found or is deleted");
        }
        return prevNote;
    }


    @Override
    public List<Note> getNotes(boolean status, String Date, boolean isAllNotes, String header) {
        utilityService.getUser(header);
        return filterNotes(status, Date, isAllNotes);
    }

    private List<Note> filterNotes(boolean status, String date, boolean isAllNotes) {
        if (isAllNotes && !status && date.equals("null")) {
            return noteDao.findAllByIsDeleteIsFalse();

        }
        if (!status && date.equals("null")) {
            return noteDao.findAllByIsArchiveIsFalseAndIsDeleteIsFalse();

        } else if (status && date.equals("null")) {
            return noteDao.findAllByIsArchiveIsTrueAndIsDeleteIsFalse();

        } else if (!status && !date.equals("null")) {
            List<Note> notes = noteDao.findAllByIsDeleteIsFalse();
            return filterNotesByDate(notes, date);
        } else {
            List<Note> notes = noteDao.findAllByIsArchiveIsTrueAndIsDeleteIsFalse();
            return filterNotesByDate(notes, date);
        }
    }

    private List<Note> filterNotesByDate(List<Note> notes, String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate localDate = LocalDate.parse(date, formatter);
        List<Note> dateNotes = new ArrayList<>();

        for (Note note : notes) {
            Instant createdAt = note.getCreatedAt();
            ZonedDateTime zonedDateTime = createdAt.atZone(ZoneId.of("UTC"));
            LocalDate noteLocalDate = zonedDateTime.toLocalDate();

            if (localDate.equals(noteLocalDate)) {
                dateNotes.add(note);
            }
        }
        return dateNotes;
    }

}
