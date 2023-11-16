package server.dto;
import lombok.Data;
import java.util.List;

@Data
public class NoteDTO {

    public NoteDTO() {
        this.title = null;
        this.content = null;
        this.isArchive = false;
        this.isDelete = false;
        this.createdAt = null;
        this.createdBy = null;
        this.id = null;
        this.ids = null;
    }

    private Long[] ids;
    private Long id;
    private String title;
    private String content;
    private boolean isArchive;
    private boolean isDelete;
    private String createdAt;
    private UserDTO createdBy;
    private List<LabelDTO> labels;
}
