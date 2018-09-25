package com.dudu.users;

import com.dudu.database.DatabaseHelper;
import com.dudu.database.DatabaseResult;
import com.dudu.database.UserServiceDataSource;
import com.dudu.users.exceptions.PasswordNotMatched;
import com.dudu.users.exceptions.UserNotFound;
import com.dudu.users.exceptions.UsernameUsed;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;

@Component
public class UserService {
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
            DatabaseResult databaseResult = databaseHelper.query(conn, sql, userId);
            if (databaseResult.isEmpty())
                throw new IllegalArgumentException("User not found: UserId=" + userId);

            return User.from(databaseResult.get(0));
        }
    }

    public User createUser(String username, String password, char role) throws IllegalArgumentException, SQLException, IllegalStateException, UsernameUsed {
        try (Connection conn = source.getConnection()) {
            // check username is user or not
            var sql = "SELECT UserId FROM Users WHERE Username = ?";
            var databaseResult = databaseHelper.query(conn, sql, username);
            if (!databaseResult.isEmpty())
                throw new UsernameUsed();

            // create user
            sql = "INSERT INTO Users(Username, Password, Role) VALUES (?,?,?) ";
            var hashed = passwordHasher.hashPassword(password);
            databaseResult = databaseHelper.updateAndGetKey(conn, sql, new String[]{"UserId"}, username, hashed, USER_ROLE_CUSTOMER);
            if (databaseResult.isEmpty())
                throw new IllegalStateException("Expect UserId generated");

            long id = databaseResult.get(0).getLong("UserId");

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
            var databaseResult = databaseHelper.query(conn, selectPassword, userId);
            if (databaseResult.isEmpty())
                throw new UserNotFound();

            var oldPasswordExpected = databaseResult.get(0).getString("Password");
            if (!oldPasswordExpected.equals(passwordHasher.hashPassword(oldPassword)))
                throw new PasswordNotMatched();

            var updatePassword = "UPDATE Users SET Password = ? WHERE UserId = ?";
            var c = databaseHelper.update(conn, updatePassword, passwordHasher.hashPassword(newPassword), userId);
            if (c != 1)
                throw new SQLException("Can't update the user");
        }
    }

}
