package server.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import server.domain.Label;
import server.dto.LabelDTO;
import server.service.LabelService;

@RestController
@RequestMapping("/labels")
public class LabelController {

    private final LabelService labelService;

    public LabelController(LabelService labelService) {
        this.labelService = labelService;
    }

    @PostMapping()
    public ResponseEntity<Label> createLabel(@RequestBody LabelDTO labelDTO, @RequestHeader("Authorization") String authorizationHeader) {
        Label label=labelService.createLabel(labelDTO.getTitle(), authorizationHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(label);
    }

    @PatchMapping()
    public ResponseEntity<String> assignLabels(@RequestBody LabelDTO labelDTO, @RequestHeader("Authorization") String authorizationHeader) {
        labelService.assignLabels(labelDTO, authorizationHeader);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
