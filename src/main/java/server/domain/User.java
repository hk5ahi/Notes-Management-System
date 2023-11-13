package server.domain;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    public User(String fullName, String username, String password, userRole userRole) {
        this.setFullName(fullName);
        this.setUsername(username);
        this.setPassword(password);
        this.setUserRole(userRole);
    }

    public enum userRole {
        Admin,
        User
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    @Column(name = "full_name", nullable = false)
    private String fullName;
    @Column(name = "username", nullable = false, unique = true)
    private String username;
    @Column(name = "password", nullable = false)
    private String password;
    @Enumerated(EnumType.STRING)
    private userRole userRole;
}

