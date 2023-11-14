package server.utilities;
import org.springframework.stereotype.Service;
import server.dao.UserDao;
import server.domain.User;
import server.dto.AuthUserDTO;
import server.exception.NotFoundException;
import server.exception.UnAuthorizedException;
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
                .orElseThrow(() -> new NotFoundException("User not found"));
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
                    .orElseThrow(() -> new UnAuthorizedException("username/password not matched"));

            return new AuthUserDTO(authenticatedUser.getUserRole(), authenticatedUser.getUsername());
        } else {
            throw new UnAuthorizedException("Auth Header is missing");
        }
    }

}
