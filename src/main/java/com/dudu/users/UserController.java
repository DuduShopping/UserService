package com.dudu.users;

import com.dudu.common.UserServiceDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;

@RestController
public class UserController {
    private static final Logger logger = LogManager.getLogger(UserController.class);
    private DataSource source;

    public UserController(UserServiceDataSource source) {
        logger.info("hihi");
        this.source = source;
    }

    @RequestMapping(path = "/user")
    public String user() {
        return "uesr";
    }
}
