package com.dudu.users;

import com.dudu.users.exceptions.PasswordNotMatched;
import com.dudu.users.exceptions.UserNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.sql.SQLException;

@RestController
public class AuthenticationController {

    private AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping(value = "/authenticate")
    public AuthResponse authenticate(@Valid AuthRequest req) {
        try {
            var token = authenticationService.login(req.getUsername(), req.getPassword());
            AuthResponse rsp = new AuthResponse();
            rsp.setToken(token);
            return rsp;
        } catch (SQLException e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (UserNotFound userNotFound) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Username not found");
        } catch (PasswordNotMatched passwordNotMatched) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Wrong password");
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

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}
