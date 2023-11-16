package server.utilities;
import org.springframework.stereotype.Service;
import server.dao.UserDao;
import server.domain.Label;
import server.domain.User;
import server.dto.AuthUserDTO;
import server.dto.LabelDTO;
import server.dto.UserDTO;
import server.exception.NotFoundException;
import server.exception.UnAuthorizedException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class UtilityService {

    private final UserDao userDao;

    public UtilityService(UserDao userDao) {
        this.userDao = userDao;
    }


    public User getUser(String authorizationHeader) throws NotFoundException {
        Map<String, String> credentials = extractCredentials(authorizationHeader);
        return Optional.of(credentials)
                .flatMap(cred -> userDao.getUserByUsernameAndPassword(cred.get("username"), cred.get("password")))
                .orElseThrow(() -> new NotFoundException("Invalid Request","User not found"));
    }

    private Map<String, String> extractCredentials(String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Basic ")) {
            String encodedCredentials = authorizationHeader.substring("Basic ".length());
            byte[] decodedCredentials = Base64.getDecoder().decode(encodedCredentials);
            String credentials = new String(decodedCredentials);
            String[] usernameAndPassword = credentials.split(":");
            String username = usernameAndPassword[0];
            String password = usernameAndPassword[1];
            Map<String, String> credential = new HashMap<>();
            credential.put("username", username);
            credential.put("password", password);
            return credential;
        }
        return Collections.emptyMap();
    }

    public AuthUserDTO getAuthUser(String authorizationHeader) {
        Map<String, String> credentials = extractCredentials(authorizationHeader);
        if (!(credentials.isEmpty())) {
            User authenticatedUser = userDao
                    .getUserByUsernameAndPassword(credentials.get("username"), credentials.get("password"))
                    .orElseThrow(() -> new UnAuthorizedException("Invalid Request","username/password not matched"));

            return new AuthUserDTO(authenticatedUser.getUserRole(), authenticatedUser.getUsername());
        } else {
            throw new UnAuthorizedException("Invalid Request","Auth Header is missing");
        }
    }
    public boolean isNumeric(String inputString) {
        try {
            Double.parseDouble(inputString);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
    public LabelDTO convertLabelToLabelDTO(Label label) {
        LabelDTO labelDTO = new LabelDTO();
        labelDTO.setId(label.getId());
        labelDTO.setTitle(label.getTitle());
        LocalDateTime localDateTime = LocalDateTime.ofInstant(label.getCreatedAt(), ZoneId.of("UTC+5"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM dd, yyyy, hh:mm:ssa ");
        String formattedTimestamp = localDateTime.format(formatter);
        labelDTO.setCreatedAt(formattedTimestamp);
        UserDTO userDTO = new UserDTO();
        userDTO.setId(label.getCreatedBy().getId());
        labelDTO.setCreatedBy(userDTO);
        return labelDTO;
    }
}
