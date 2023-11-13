package server.service.Implementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.dao.NoteDao;
import server.dao.UserDao;
import server.domain.Note;
import server.domain.User;
import server.dto.AuthUserDTO;
import server.dto.NoteDTO;
import server.exception.BadRequestException;
import server.exception.NotFoundException;
import server.service.NoteService;
import server.utilities.UtilityService;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

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
        Optional<Note> existedNote = noteDao.findByNoteId(note.getId());
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
                throw new BadRequestException("Already Archived Note");

            }
        }

    }

    private void validateIfUserCanDeleteNote(NoteDTO noteDTO, String authorizationHeader) {
        AuthUserDTO authUserDTO = utilityService.getAuthUser(authorizationHeader);
        if (noteDTO.isDelete() && !authUserDTO.getUserRole().equals(User.userRole.Admin)) {
            throw new BadRequestException("Only Admin can delete the note");
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
        Note prevNote = noteDao
                .findByNoteId(noteDTO.getId())
                .orElseThrow(NotFoundException::new);
        if (prevNote.isDelete()) {
            throw new NotFoundException("Note not found or is deleted");
        }
        validateIfUserCanArchiveNote(noteDTO, prevNote);
        validateIfUserCanDeleteNote(noteDTO, authorizationHeader);
        Note newNote = new Note();
        BeanUtils.copyProperties(prevNote, newNote);
        Note updatedNote = copyNote(prevNote, noteDTO);
        updatedNote.setArchive(noteDTO.isArchive() ? noteDTO.isArchive() : prevNote.isArchive());
        updatedNote.setDelete(noteDTO.isDelete() ? noteDTO.isDelete() : prevNote.isDelete());
        noteDao.save(updatedNote);
    }
}
