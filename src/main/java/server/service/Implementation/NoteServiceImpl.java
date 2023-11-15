package server.service.Implementation;

import org.aspectj.weaver.ast.Not;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.dao.NoteDao;
import server.domain.Note;
import server.domain.User;
import server.dto.AuthUserDTO;
import server.dto.NoteDTO;
import server.dto.UserDTO;
import server.exception.BadRequestException;
import server.exception.ForbiddenException;
import server.exception.InvalidDateFormatException;
import server.exception.NotFoundException;
import server.service.NoteService;
import server.utilities.UtilityService;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;

import static java.lang.Boolean.parseBoolean;

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
    public NoteDTO createNote(NoteDTO note, String header) {
        User loggedInUser = utilityService.getUser(header);
        Note noteToSave = new Note(note.getTitle(), note.getContent(), note.isArchive(), note.isDelete());
        Instant instant = Instant.now();
        noteToSave.setCreatedAt(instant);
        noteToSave.setCreatedBy(loggedInUser);
        Optional<Note> existedNote = noteDao.findByTitleAndIsDeleteIsFalse(note.getTitle());
        if (existedNote.isPresent() && !existedNote.get().isDelete()) {
            throw new BadRequestException("Invalid Request", "The note already exists with same title " + noteToSave.getTitle());
        } else {
            noteDao.save(noteToSave);
            return convertNoteToNoteDTO(noteToSave);

        }
    }

    private void validateIfUserCanArchiveNote(Note existedNote) {
        if (existedNote.isArchive()) {
            log.error("Already Archived Note");
        }
    }

    private void validateIfUserCanDeleteNote(String authorizationHeader) {
        AuthUserDTO authUserDTO = utilityService.getAuthUser(authorizationHeader);
        if (!authUserDTO.getUserRole().equals(User.userRole.Admin)) {
            throw new ForbiddenException("Invalid Request", "Only Admin can delete the note");
        }
    }

    @Override
    @Transactional
    public NoteDTO editNote(String authorizationHeader, NoteDTO noteDTO) {
        utilityService.getUser(authorizationHeader);
        Note prevNote = validateIfNoteExists(noteDTO.getId());
        prevNote.setTitle(noteDTO.getTitle());
        prevNote.setContent(noteDTO.getContent());
        noteDao.save(prevNote);
        return convertNoteToNoteDTO(prevNote);
    }

    @Override
    @Transactional
    public void archiveNote(String authorizationHeader, NoteDTO noteDTO) {
        utilityService.getUser(authorizationHeader);
        for (Long id : noteDTO.getIds()) {
            Note prevNote = validateIfNoteExists(id);
            validateIfUserCanArchiveNote(prevNote);
            prevNote.setArchive(true);
            noteDao.save(prevNote);
        }
    }

    @Override
    @Transactional
    public void deleteNote(String authorizationHeader, NoteDTO noteDTO) {
        for (Long id : noteDTO.getIds()) {
            Note prevNote = validateIfNoteExists(id);
            validateIfUserCanDeleteNote(authorizationHeader);
            prevNote.setDelete(true);
            noteDao.save(prevNote);
        }
    }

    private Note validateIfNoteExists(Long id) {
        return noteDao
                .findByIdAndIsDeleteIsFalse(id)
                .orElseThrow(() -> new NotFoundException("Invalid Request", "Note not found or is deleted"));
    }

    private boolean validateIfStatusIsBoolean(String status) {
        boolean isArchive;
        try {
            return isArchive = checkIfBoolean(status);
        } catch (IllegalArgumentException e) {
            throw new InvalidDateFormatException(e.getMessage(), "Invalid value for 'isArchive'. Please provide a valid boolean value.");
        }
    }

    private boolean checkIfBoolean(String value) {
        if (value != null) {
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("1")) {
                return true;
            } else if (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("0")) {
                return false;
            } else {
                throw new InvalidDateFormatException("Invalid Status", "Invalid value for 'isArchive'. Please provide a valid boolean value.");
            }
        } else {
            return false;
        }
    }

    @Override
    public List<NoteDTO> getNotes(String status, String Date, String header) {
        utilityService.getUser(header);
        if (!status.equals("")) {
            boolean isArchive = validateIfStatusIsBoolean(status);
            return filterNotes(isArchive, Date);
        } else {
            if (Date.equals("null")) {
                return sendNotesToNotesDTO(noteDao.findAllByIsDeleteIsFalse());
            } else {
                List<Note> notes = noteDao.findAllByIsDeleteIsFalse();
                return sendNotesToNotesDTO(filterNotesByDate(notes, Date));
            }
        }
    }

    private List<NoteDTO> sendNotesToNotesDTO(List<Note> notes) {
        List<NoteDTO> noteDTOs = new ArrayList<>();
        for (Note note : notes) {
            noteDTOs.add(convertNoteToNoteDTO(note));
        }
        return noteDTOs;
    }

    private List<NoteDTO> filterNotes(boolean status, String date) {
        if (!status && date.equals("null")) {
            return sendNotesToNotesDTO(noteDao.findAllByIsArchiveIsFalseAndIsDeleteIsFalse());
        } else if (status && date.equals("null")) {
            return sendNotesToNotesDTO(noteDao.findAllByIsArchiveIsTrueAndIsDeleteIsFalse());
        } else if (!status && !date.equals("null")) {
            List<Note> notes = noteDao.findAllByIsArchiveIsFalseAndIsDeleteIsFalse();
            return sendNotesToNotesDTO(filterNotesByDate(notes, date));
        } else {
            List<Note> notes = noteDao.findAllByIsArchiveIsTrueAndIsDeleteIsFalse();
            return sendNotesToNotesDTO(filterNotesByDate(notes, date));
        }
    }

    private List<Note> filterNotesByDate(List<Note> notes, String date) {
        DateTimeFormatter expectedFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        try {
            LocalDate localDate = LocalDate.parse(date, expectedFormatter);
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
        } catch (DateTimeParseException e) {
            throw new InvalidDateFormatException(e.getMessage(), "Invalid date format. Please use the pattern dd-MM-yyyy.");
        }
    }

    private NoteDTO convertNoteToNoteDTO(Note note) {
        NoteDTO noteDTO = new NoteDTO();
        noteDTO.setId(note.getId());
        noteDTO.setTitle(note.getTitle());
        noteDTO.setContent(note.getContent());
        noteDTO.setArchive(note.isArchive());
        noteDTO.setDelete(note.isDelete());
        noteDTO.setCreatedAt(note.getCreatedAt());
        UserDTO userDTO = new UserDTO();
        userDTO.setId(note.getCreatedBy().getId());
        noteDTO.setCreatedBy(userDTO);
        noteDTO.setLabels(note.getLabels());
        return noteDTO;
    }
}
