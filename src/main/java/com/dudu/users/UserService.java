package com.dudu.users;

import com.dudu.database.DatabaseHelper;
import com.dudu.database.UserServiceDataSource;
import com.dudu.database.ZetaMap;
import com.dudu.users.exceptions.PasswordNotMatched;
import com.dudu.users.exceptions.UserNotFound;
import com.dudu.users.exceptions.UsernameUsed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Component
public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);
    public static final char USER_ROLE_CUSTOMER = 'C';

    private UserServiceDataSource source;
    private DatabaseHelper databaseHelper;
    private PasswordHasher passwordHasher;

    public UserService(UserServiceDataSource source) {
        this.source = source;
        this.databaseHelper = DatabaseHelper.getHelper();
        this.passwordHasher = PasswordHasher.getInstance();
    }

    public User getUser(long userId) throws IllegalArgumentException, SQLException, UserNotFound {
        try (Connection conn = source.getConnection()) {
            String sql = "SELECT * FROM Users WHERE UserId = ?";
            List<ZetaMap> zetaMaps = databaseHelper.execToZetaMaps(conn, sql, userId);
            if (zetaMaps.size() == 0)
                throw new IllegalArgumentException("User not found: UserId=" + userId);

            return User.from(zetaMaps.get(0));
        }
    }

    public User createUser(String username, String password, char role) throws IllegalArgumentException, SQLException, IllegalStateException, UsernameUsed {
        try (Connection conn = source.getConnection()) {
            // check username is user or not
            var sql = "SELECT UserId FROM Users WHERE Username = ?";
            var zmaps = databaseHelper.execToZetaMaps(conn, sql, username);
            if (zmaps.size() != 0)
                throw new UsernameUsed();

            // create user
            sql = "INSERT INTO Users(Username, Password, Role) VALUES (?,?,?) ";
            var hashed = passwordHasher.hashPassword(password);
            zmaps = databaseHelper.execUpdateToZetaMaps(conn, sql, new String[]{"UserId"}, username, hashed, USER_ROLE_CUSTOMER);
            if (zmaps.size() == 0)
                throw new IllegalStateException("Expect UserId generated");

            long id = zmaps.get(0).getLong("UserId");

            // getting the user
            try {
                return getUser(id);
            } catch (Exception e) {
                throw new IllegalStateException("Expecting UserId=" + id + " created");
            }
        }
    }

    public void updatePassword(long userId, String oldPassword, String newPassword) throws UserNotFound, PasswordNotMatched, SQLException {
        try (Connection conn = source.getConnection()) {
            var selectPassword = "SELECT Password FROM Users WHERE UserId =?";
            var zetaMapList = databaseHelper.execToZetaMaps(conn, selectPassword, userId);
            if (zetaMapList.size() == 0)
                throw new UserNotFound();

            var oldPasswordExpected = zetaMapList.get(0).getString("Password");
            if (!oldPasswordExpected.equals(passwordHasher.hashPassword(oldPassword)))
                throw new PasswordNotMatched();

            var updatePassword = "UPDATE Users SET Password = ? WHERE UserId = ?";
            var c = databaseHelper.execUpdate(conn, updatePassword, passwordHasher.hashPassword(newPassword), userId);
            if (c != 1)
                throw new SQLException("Can't update the user");
        }
    }

}
