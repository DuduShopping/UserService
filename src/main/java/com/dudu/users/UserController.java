package com.dudu.users;

import com.dudu.common.CryptoUtil;
import com.dudu.common.UserServiceDataSource;
import com.dudu.database.DatabaseHelper;
import com.dudu.database.ZetaMap;
import com.dudu.exception.BadRequestException;
import com.dudu.exception.InternalServerException;
import com.dudu.exception.NotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
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
    public User createUser(@Valid UserCreation req) {
        logger.info("creating a user: " + req.toString());
        try (Connection conn = source.getConnection()) {
            var sql = "INSERT INTO Users(Login, Password) VALUES (?,?) ";
            var hashed = hashPassword(req.getPassword());
            var zmaps = databaseHelper.execUpdateToZetaMaps(conn, sql, new String[]{"UserId"}, req.getLogin(), hashed);
            if (zmaps.size() == 0)
                throw new InternalServerException("");

            long id = zmaps.get(0).getLong("UserId");
            return getUser(id);
        } catch (Exception e) {
            logger.info(e);
            throw new BadRequestException("Failed to create user", e);
        }
    }

    private User getUser(long userId) {
        try (Connection conn = source.getConnection()) {
            String sql = "SELECT * FROM Users WHERE UserId = ?";
            List<ZetaMap> zetaMaps = databaseHelper.execToZetaMaps(conn, sql, userId);
            if (zetaMaps.size() == 0)
                throw new IllegalArgumentException("User not found: UserId="+userId);

            return User.from(zetaMaps.get(0));
        } catch (SQLException e) {
            logger.info(e);
            throw new IllegalArgumentException("Failed to get user", e);
        }
    }

    private String hashPassword(String password) {
        return CryptoUtil.sha256base64(SALT + password);
    }

    public static class UpdatePassword {
        @NotEmpty
        private String oldPassword;

        @NotEmpty
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
        @NotEmpty
        private String login;

        @NotEmpty
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
