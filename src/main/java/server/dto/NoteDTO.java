package server.dto;
import lombok.Data;
import server.domain.Label;
import java.time.Instant;
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
        this.noteId=null;
    }

    private Long[] id;
    private Long noteId;
    private String title;
    private String content;
    private boolean isArchive;
    private boolean isDelete;
    private Instant createdAt;
    private String createdBy;
    private List<Label> labels;

}
