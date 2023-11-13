package server.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import server.domain.User;

@Data // for getter and setters
@AllArgsConstructor // for parametrized Constructor
public class AuthUserDTO {
    private User.userRole userRole;
    private String username;
}