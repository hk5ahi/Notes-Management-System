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
import server.exception.NotFoundException;
import server.exception.UnAuthorizedException;
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
    public void createNote(NoteDTO note, String header) {
        User loggedInUser = utilityService.getUser(header);
        Note noteToSave = new Note(note.getTitle(), note.getContent(), note.isArchive(), note.isDelete());
        Instant instant = Instant.now();
        noteToSave.setCreatedAt(instant);
        noteToSave.setCreatedBy(loggedInUser);
        Optional<Note> existedNote = noteDao.findByTitleAndIsDeleteIsFalse(note.getTitle());
        if (existedNote.isPresent() && !existedNote.get().isDelete()) {
            log.error("The note already exists with same title {}", noteToSave.getTitle());
            throw new BadRequestException("The note can not be created");
        } else {
            noteDao.save(noteToSave);
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
            throw new UnAuthorizedException("Only Admin can delete the note");
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
    public void updateNote(String authorizationHeader, NoteDTO noteDTO) {
        utilityService.getUser(authorizationHeader);
        validateIfUserCanArchiveAndDeleteNote(noteDTO);
        updateNoteEntries(noteDTO, authorizationHeader);
    }

    private void updateNoteEntries(NoteDTO noteDTO, String authorizationHeader) {
        if (!noteDTO.isArchive() && !noteDTO.isDelete()) {
            editNoteData(noteDTO);
        } else if (noteDTO.isArchive() && !noteDTO.isDelete()) {
            archiveEachNote(noteDTO);
        } else {
            deleteEachNote(noteDTO, authorizationHeader);
        }
    }

    private void archiveEachNote(NoteDTO noteDTO) {
        for (Long id : noteDTO.getId()) {
            Note prevNote = validateIfNoteExists(id);
            validateIfUserCanArchiveNote(noteDTO, prevNote);
            prevNote.setArchive(noteDTO.isArchive());
            noteDao.save(prevNote);
        }
    }

    private void deleteEachNote(NoteDTO noteDTO, String authorizationHeader) {

        for (Long id : noteDTO.getId()) {
            Note prevNote = validateIfNoteExists(id);
            validateIfUserCanDeleteNote(noteDTO, authorizationHeader);
            prevNote.setDelete(noteDTO.isDelete());
            noteDao.save(prevNote);
        }
    }

    private void editNoteData(NoteDTO noteDTO) {
        Long id = validateAndGetNoteId(noteDTO);
        Note prevNote = validateIfNoteExists(id);
        Note newNote = new Note();
        BeanUtils.copyProperties(prevNote, newNote);
        Note updatedNote = copyNote(prevNote, noteDTO);
        updatedNote.setArchive(noteDTO.isArchive() ? noteDTO.isArchive() : prevNote.isArchive());
        updatedNote.setDelete(noteDTO.isDelete() ? noteDTO.isDelete() : prevNote.isDelete());
        noteDao.save(updatedNote);
    }

    private Long validateAndGetNoteId(NoteDTO noteDTO) {
        if (noteDTO.getId() == null || noteDTO.getId().length != 1) {
            throw new BadRequestException("Invalid input: id should be an array with exactly one value");
        }
        Long[] noteId = noteDTO.getId();
        return noteId[0];
    }

    private Note validateIfNoteExists(Long id) {
        Note prevNote = noteDao
                .findByNoteId(id)
                .orElseThrow(NotFoundException::new);
        if (prevNote.isDelete()) {
            throw new NotFoundException("Note not found or is deleted");
        }
        return prevNote;
    }

    private void validateIfUserCanArchiveAndDeleteNote(NoteDTO noteDTO) {
        if (noteDTO.isArchive() && noteDTO.isDelete()) {
            throw new BadRequestException("Note can not be archived and deleted at the same time");
        }
    }

    @Override
    public List<NoteDTO> getNotes(boolean status, String Date, boolean isAllNotes, String header) {
        utilityService.getUser(header);
        return filterNotes(status, Date, isAllNotes);
    }

    private List<NoteDTO> filterNotes(boolean status, String date, boolean isAllNotes) {
        if (isAllNotes && !status && date.equals("null")) {
            List<Note> notes = noteDao.findAllByIsDeleteIsFalse();
            return convertToNoteDTO(notes);
        }
        if (!status && date.equals("null")) {
            List<Note> notes = noteDao.findAllByIsArchiveIsFalseAndIsDeleteIsFalse();
            return convertToNoteDTO(notes);
        } else if (status && date.equals("null")) {
            List<Note> notes = noteDao.findAllByIsArchiveIsTrueAndIsDeleteIsFalse();
            return convertToNoteDTO(notes);
        } else if (!status && !date.equals("null")) {
            List<Note> notes = noteDao.findAllByIsDeleteIsFalse();
            return convertToNoteDTO(filterNotesByDate(notes, date));
        } else {
            List<Note> notes = noteDao.findAllByIsArchiveIsTrueAndIsDeleteIsFalse();
            return convertToNoteDTO(filterNotesByDate(notes, date));
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

    private List<NoteDTO> convertToNoteDTO(List<Note> notes) {
        List<NoteDTO> noteDTOS = new ArrayList<>();
        for (Note note : notes) {
            NoteDTO noteDTO = new NoteDTO();
            noteDTO.setNoteId(note.getNoteId());
            noteDTO.setTitle(note.getTitle());
            noteDTO.setContent(note.getContent());
            noteDTO.setCreatedAt(note.getCreatedAt());
            noteDTO.setCreatedBy(note.getCreatedBy().getUsername());
            noteDTO.setLabels(note.getLabels());
            noteDTOS.add(noteDTO);
        }
        return noteDTOS;
    }

}
