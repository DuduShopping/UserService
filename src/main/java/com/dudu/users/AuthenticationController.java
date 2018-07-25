package com.dudu.users;

import com.dudu.oauth.Claims;
import com.dudu.oauth.LoggedUser;
import com.dudu.oauth.OAuthFilter;
import com.dudu.oauth.TokenDecoder;
import com.dudu.users.exceptions.InvalidToken;
import com.dudu.users.exceptions.PasswordNotMatched;
import com.dudu.users.exceptions.UserNotFound;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.sql.SQLException;

@RestController
public class AuthenticationController {
    private static final Logger logger = LogManager.getLogger(AuthenticationController.class);
    private AuthenticationService authenticationService;
    private TokenDecoder tokenDecoder;

    @Autowired
    private HttpServletRequest httpServletRequest;

    public AuthenticationController(AuthenticationService authenticationService, TokenDecoder tokenDecoder) {
        this.authenticationService = authenticationService;
        this.tokenDecoder = tokenDecoder;
    }

    @PostMapping(value = "/auth/authenticate")
    public AuthResponse authenticate(@Valid AuthRequest req) {
        try {
            var token = authenticationService.login(req.getUsername(), req.getPassword());
            AuthResponse rsp = new AuthResponse();
            Claims claims = tokenDecoder.getClaims(token);
            rsp.setToken(token);
            rsp.setExpiresAt(claims.getExp());
            rsp.setIssuedAt(claims.getIat());
            return rsp;
        } catch (SQLException e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (UserNotFound userNotFound) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Username not found");
        } catch (PasswordNotMatched passwordNotMatched) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Wrong password");
        } catch (Exception e) {
            logger.error("Unexpected exception: ", e);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping(value = "/auth/refreshToken")
    public AuthResponse refreshToken() {
        try {
            LoggedUser loggedUser = (LoggedUser) httpServletRequest.getAttribute(OAuthFilter.LOGGED_USER);
            var newToken = authenticationService.refreshToken(loggedUser.getToken());
            AuthResponse rsp = new AuthResponse();
            Claims claims = tokenDecoder.getClaims(newToken);
            rsp.setToken(newToken);
            rsp.setExpiresAt(claims.getExp());
            rsp.setIssuedAt(claims.getIat());
            return rsp;
        } catch (InvalidToken invalidToken) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Invalid token");
        } catch (SQLException e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (Exception e) {
            logger.error("Unexpected exception: ", e);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public static class AuthRequest {
        @NotEmpty
        private String username;

        @NotEmpty
        private String password;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }
    }

    public static class AuthResponse {
        private String token;

        private long expiresAt;

        private long issuedAt;

        public long getExpiresAt() {
            return expiresAt;
        }

        public void setExpiresAt(long expiresAt) {
            this.expiresAt = expiresAt;
        }

        public long getIssuedAt() {
            return issuedAt;
        }

        public void setIssuedAt(long issuedAt) {
            this.issuedAt = issuedAt;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
