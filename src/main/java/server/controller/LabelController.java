package server.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
    public ResponseEntity<String> createLabel(@RequestBody String title, @RequestHeader("Authorization") String authorizationHeader) {
        labelService.createLabel(title, authorizationHeader);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PatchMapping()
    public ResponseEntity<String> assignLabels(@RequestBody LabelDTO labelDTO, @RequestHeader("Authorization") String authorizationHeader) {
        labelService.assignLabels(labelDTO, authorizationHeader);
        return ResponseEntity.status(HttpStatus.OK).build();
    }
}
