package com.dudu.users;

import com.dudu.oauth.LoggedUser;
import com.dudu.oauth.OAuthFilter;
import com.dudu.users.exceptions.PasswordNotMatched;
import com.dudu.users.exceptions.UserNotFound;
import com.dudu.users.exceptions.UsernameUsed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.sql.SQLException;

@RestController
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);
    private UserService userService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PutMapping(value = "/user/password")
    public void updatePassword(@Valid UpdatePassword req) {
        logger.debug("password");
        try {
            LoggedUser user = (LoggedUser) httpServletRequest.getAttribute(OAuthFilter.LOGGED_USER);
            userService.updatePassword(user.getUserId(), req.oldPassword, req.newPassword);
        } catch (SQLException e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (UserNotFound userNotFound) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        } catch (PasswordNotMatched passwordNotMatched) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Password is wrong");
        }

    }

    @PostMapping(value = "/user")
    public User createUser(@Valid UserCreation req) {
        try {
            return userService.createUser(req.getUsername(), req.getPassword());
        } catch (SQLException | IllegalStateException e) {
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Failed to create user");
        } catch (UsernameUsed e) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Username is taken");
        }
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


}
