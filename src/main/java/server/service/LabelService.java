package server.service;
import server.domain.Label;
import server.dto.LabelDTO;

public interface LabelService {

    Label createLabel(String title, String header);
    void assignLabels(LabelDTO labelDTO, String header);
}
