package server.dto;
import lombok.Data;
import java.time.Instant;

@Data
public class NoteDTO {

    public NoteDTO() {
        this.title = null;
        this.content = null;
        this.isArchive = false;
        this.isDelete = false;
        this.createdAt = null;
        this.createdBy = null;
    }

    private Long id;
    private String title;
    private String content;
    private boolean isArchive;
    private boolean isDelete;
    private Instant createdAt;
    private String createdBy;

}
