package com.dudu.users;

import com.dudu.common.UserServiceDataSource;
import com.dudu.database.DatabaseHelper;
import com.dudu.database.ZetaMap;
import com.dudu.exception.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@RestController
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);
    private static final String SALT = "p@om^bcad3&yjena[jd!~si42*)[jdjk";
    private DataSource source;
    private DatabaseHelper databaseHelper;

    public UserController(UserServiceDataSource source) {
        logger.info("hihi");
        this.source = source;
        this.databaseHelper = DatabaseHelper.getHelper();
    }

    @RequestMapping(path = "/user/password", method = RequestMethod.GET)
    public void user(UpdatePassword req) {
        return;
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public User createUser(UserCreation req) {
        logger.info("creating a user: " + req.toString());
        try (Connection conn = source.getConnection()) {
            var sql = "INSERT INTO Users(Login, Password) VALUES (?,?) ";
            var zmaps = databaseHelper.execUpdateToZetaMaps(conn, sql, new String[]{"UserId"}, req.getLogin(), req.getPassword());
            if (zmaps.size() == 0)
                throw new NotFoundException("");

            long id = zmaps.get(0).getLong("UserId");
            return getUser(id);
        } catch (SQLException e) {
            logger.info(e);
            throw new NotFoundException("Failed to create user");
        }
    }

    private User getUser(long userId) {
        try (Connection conn = source.getConnection()) {
            String sql = "SELECT * FROM Users WHERE UserId = ?";
            List<ZetaMap> zetaMaps = databaseHelper.execToZetaMaps(conn, sql, userId);
            if (zetaMaps.size() == 0)
                throw new NotFoundException("User not found: UserId="+userId);

            return User.from(zetaMaps.get(0));
        } catch (SQLException e) {
            logger.info(e);
            throw new NotFoundException("Failed to get user");
        }
    }

    public static class UpdatePassword {
        private String oldPassword;
        private String newPassword;

        public String getOldPassword() {
            return oldPassword;
        }

        public void setOldPassword(String oldPassword) {
            this.oldPassword = oldPassword;
        }

        public String getNewPassword() {
            return newPassword;
        }

        public void setNewPassword(String newPassword) {
            this.newPassword = newPassword;
        }
    }

    public static class UserCreation {
        private String login;
        private String password;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }


}
