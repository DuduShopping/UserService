package com.dudu.users;

import com.dudu.database.DatabaseHelper;
import com.dudu.database.UserServiceDataSource;
import com.dudu.oauth.Claims;
import com.dudu.oauth.TokenDecoder;
import com.dudu.oauth.TokenIssuer;
import com.dudu.users.exceptions.InvalidToken;
import com.dudu.users.exceptions.PasswordNotMatched;
import com.dudu.users.exceptions.UserNotFound;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class AuthenticationService {
    private TokenIssuer tokenIssuer;
    private TokenDecoder tokenDecoder;
    private DataSource source;
    private PasswordHasher passwordHasher;
    private DatabaseHelper databaseHelper;

    public AuthenticationService(TokenIssuer tokenIssuer, TokenDecoder tokenDecoder, UserServiceDataSource source) {
        this.tokenIssuer = tokenIssuer;
        this.tokenDecoder = tokenDecoder;
        this.source = source;
        this.passwordHasher = PasswordHasher.getInstance();
        this.databaseHelper = DatabaseHelper.getHelper();
    }

    /**
     *
     * @param username
     * @param password
     * @return token
     * @throws UserNotFound
     * @throws PasswordNotMatched
     * @throws SQLException
     */
    public String login(String username, String password) throws UserNotFound, PasswordNotMatched, SQLException {
        try (Connection conn = source.getConnection()) {
            String selectUser = "SELECT UserId, Role FROM Users WHERE Username = ?";
            var databaseResult = databaseHelper.query(conn, selectUser, username);
            if (databaseResult.isEmpty())
                throw new UserNotFound();

            var userId = databaseResult.get(0).getLong("UserId");
            String checkPassword = "SELECT Username FROM Users WHERE UserId = ? AND Password = ?";
            if (!databaseHelper.notEmpty(conn, checkPassword, userId, passwordHasher.hashPassword(password)))
                throw new PasswordNotMatched();

            // authenticated, issue token.
            var role = databaseResult.get(0).getChar("Role");
            if (role == 'C')
                return tokenIssuer.issue(userId, Arrays.asList("Customer"));
            else if (role == 'S')
                return tokenIssuer.issue(userId, Arrays.asList("SaleAgent"));
            else
                throw new UserNotFound();
        }
    }

    public String refreshToken(String token) throws InvalidToken {
        long userId;
        List<String> scopeList;

        try {
            Claims claims = tokenDecoder.getClaims(token);
            userId = claims.getUserId();
            scopeList = new ArrayList<>(claims.getScopes());
        } catch (Exception e) {
            throw new InvalidToken();
        }

        // good
        return tokenIssuer.issue(userId, scopeList);
    }
}
