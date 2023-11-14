package server.domain;
import java.time.Instant;
import java.util.List;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notes")
@Data
@NoArgsConstructor
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
    @Column(name = "title", nullable = false, length = 25, columnDefinition = "TEXT", unique = true)
    private String title;
    @Column(name = "content", columnDefinition = "TEXT")
    private String content;
    @Column(name = "is_archive", nullable = false)
    private boolean isArchive;
    @ManyToMany
    @JoinTable(name = "note_label",
            joinColumns = @JoinColumn(name = "note_id"),
            inverseJoinColumns = @JoinColumn(name = "label_id"))
    private List<Label> labels;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    @Column(name = "is_delete", nullable = false)
    private boolean isDelete;

    public Note(String title, String content, boolean isArchive, boolean isDelete) {
        this.setTitle(title);
        this.setContent(content);
        this.setArchive(isArchive);
        this.setDelete(isDelete);
    }

}
