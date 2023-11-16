package server.service.Implementation;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.dao.UserDao;
import server.domain.User;
import server.exception.BadRequestException;
import server.exception.InvalidDateFormatException;
import server.service.UserService;
import server.utilities.UtilityService;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final UtilityService utilityService;

    public UserServiceImpl(UserDao userDao, UtilityService utilityService) {
        this.userDao = userDao;
        this.utilityService = utilityService;
    }

    @Override
    @Transactional
    public void initializeUsers(List<User> usersList) {
        for (User user : usersList) {
            if (!userDao.existsByUsername(user.getUsername())) {
                userDao.save(user);
            } else {
                throw new BadRequestException("Invalid Request", "The user already exists with username :" + user.getUsername());
            }
        }
    }

    @Override
    public void validateIfUserIsNotNull(List<User> usersList) {
        for (User user : usersList) {
            if (user.getFullName() == null) {
                throw new InvalidDateFormatException("Invalid Request", "Full name cannot be null or incorrect format");
            } else if (user.getUsername() == null) {
                throw new InvalidDateFormatException("Invalid Request", "User Name cannot be null or incorrect format");
            } else if (user.getPassword() == null) {
                throw new InvalidDateFormatException("Invalid Request", "Password cannot be null or incorrect format");
            } else if (user.getUserRole() == null)
                throw new InvalidDateFormatException("Invalid Request", "User Role cannot be null or incorrect format");
        }
    }

    @Override
    public void validateIfUserIsInCorrectFormat(List<User> usersList) {
        for (User user : usersList) {
            if (utilityService.isNumeric(user.getFullName())) {
                throw new InvalidDateFormatException("Invalid Request", "Full name cannot be a number");
            } else if (utilityService.isNumeric(user.getUsername())) {
                throw new InvalidDateFormatException("Invalid Request", "User Name cannot be a number");
            } else if (utilityService.isNumeric(String.valueOf(user.getUserRole()))) {
                throw new InvalidDateFormatException("Invalid Request", "User Role cannot be a number");
            }
        }
    }
}
