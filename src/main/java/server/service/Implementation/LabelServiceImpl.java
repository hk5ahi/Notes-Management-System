package server.service.Implementation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import server.dao.LabelDao;
import server.dao.NoteDao;
import server.domain.Label;
import server.domain.Note;
import server.domain.User;
import server.dto.LabelDTO;
import server.exception.BadRequestException;
import server.exception.NotFoundException;
import server.service.LabelService;
import server.utilities.UtilityService;

import java.time.Instant;

@Service
public class LabelServiceImpl implements LabelService {

    private final UtilityService utilityService;
    private final LabelDao labelDao;
    private final NoteDao noteDao;

    private final Logger log = LoggerFactory.getLogger(LabelServiceImpl.class);

    public LabelServiceImpl(UtilityService utilityService, LabelDao labelDao, NoteDao noteDao) {
        this.utilityService = utilityService;
        this.labelDao = labelDao;
        this.noteDao = noteDao;
    }

    @Override
    public Label createLabel(String title, String header) {
        User loggedInUser = utilityService.getUser(header);
        if (labelDao.findByTitle(title).isPresent()) {
            throw new BadRequestException("Invalid Request", "Label already exists");
        } else {
            Label labelToSave = new Label(title);
            labelToSave.setCreatedBy(loggedInUser);
            Instant instant = Instant.now();
            labelToSave.setCreatedAt(instant);
            labelDao.save(labelToSave);
            return labelToSave;
        }
    }

    @Override
    public void assignLabels(LabelDTO labelDTO, String header) {
        utilityService.getUser(header);
        Label label = labelDao.findById(labelDTO.getLabelId().toString()).orElseThrow(() -> new NotFoundException("Invalid Request", "Label not found"));
        for (Long id : labelDTO.getNoteIds()) {
            Note note = noteDao.findByIdAndIsDeleteIsFalse(id).orElseThrow(() -> new NotFoundException("Invalid Request", "Note not found"));
            if (note.getLabels().contains(label)) {
                log.error("Label is already assigned to the note");
            } else {
                note.getLabels().add(label);
                noteDao.save(note);
            }
        }
    }
}
