package server.service.Implementation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.dao.UserDao;
import server.domain.User;
import server.exception.BadRequestException;
import server.service.UserService;
import java.util.Arrays;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;
    private final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    @Transactional
    public void initializeUsers(List<User> usersList) {


        for (User user : usersList) {
            if (!userDao.existsByUsername(user.getUsername())) {
                userDao.save(user);
            } else {
                throw new BadRequestException("Invalid Request","The user already exists with username :"+ user.getUsername());
            }
        }
    }

}
