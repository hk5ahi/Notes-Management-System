package server.service;
import server.dto.LabelDTO;

public interface LabelService {

    void createLabel(String title, String header);
    void assignLabels(LabelDTO labelDTO, String header);
}
