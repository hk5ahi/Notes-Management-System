package server.domain;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Data
@NoArgsConstructor
@Entity
@Table(name = "labels")

public class Label {

    public Label(String title) {
        this.title = title;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long labelId;
    @Column(name = "title", nullable = false, length = 25, columnDefinition = "TEXT", unique = true)
    private String title;
    @ManyToOne
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
}
