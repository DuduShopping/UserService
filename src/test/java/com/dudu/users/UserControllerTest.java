package com.dudu.users;

import com.dudu.common.UserServiceDataSource;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(value = SpringRunner.class)
@ContextConfiguration(classes = {com.dudu.DataSourceConfiguration.class})
public class UserControllerTest {

    @Autowired
    private UserServiceDataSource source;

    private UserController userController;

    @Before
    public void setup() {
        Assume.assumeTrue(source != null);
        userController = new UserController(source);
    }

    @Test
    public void createUser() throws Exception {
        UserController.UserCreation userCreation = new UserController.UserCreation();
        userCreation.setLogin("jack3");
        userCreation.setPassword("test123");

        User user = userController.createUser(userCreation);
    }
}