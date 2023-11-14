package server.dto;
import lombok.Data;

@Data
public class LabelDTO {

    private Long labelId;
    private Long[] NoteIds;
}
