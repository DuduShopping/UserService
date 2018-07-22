package com.dudu.users;

import com.dudu.users.exceptions.UsernameUsed;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.sql.SQLException;

@RestController
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);
    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @RequestMapping(path = "/user/password", method = RequestMethod.GET)
    public void user(UpdatePassword req) {
        return;
    }

    @RequestMapping(value = "/user", method = RequestMethod.POST)
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
