package com.dudu.users;

import com.dudu.oauth.LoggedUser;
import com.dudu.oauth.OAuthFilter;
import com.dudu.users.exceptions.PasswordNotMatched;
import com.dudu.users.exceptions.UserNotFound;
import com.dudu.users.exceptions.UsernameUsed;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
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
            logger.warn("", e);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (UserNotFound userNotFound) {
            logger.warn("", userNotFound);
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST);
        } catch (PasswordNotMatched passwordNotMatched) {
            logger.warn("", passwordNotMatched);
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Password is wrong");
        }

    }

    @PostMapping(value = "/user/customer")
    public Map<String, Object> createUser(@Valid CustomerUserCreation req) {
        Map<String, Object> result = new LinkedHashMap<>();
        try {
            User user = userService.createUser(req.getUsername(), req.getPassword(), UserService.USER_ROLE_CUSTOMER);
            result.put("ok", true);
            return result;
        } catch (SQLException | IllegalStateException e) {
            logger.warn("", e);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (IllegalArgumentException e) {
            logger.warn("", e);
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Failed to create user");
        } catch (UsernameUsed e) {
            logger.warn("", e);
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

    public static class CustomerUserCreation {
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
