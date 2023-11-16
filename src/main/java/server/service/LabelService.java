package server.service;
import server.dto.LabelDTO;

public interface LabelService {

    LabelDTO createLabel(String title, String header);

    void assignLabels(LabelDTO labelDTO, String header);

    void validateIfLabelIsNotNull(LabelDTO labelDTO);
}
