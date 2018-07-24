package com.dudu.users;

import com.dudu.database.DataSourceConfiguration;
import com.dudu.database.UserServiceDataSource;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(value = SpringRunner.class)
@ContextConfiguration(classes = {DataSourceConfiguration.class})
public class UserServiceTest {

    @Autowired
    private UserServiceDataSource dataSource;

    private UserService userService;

    @Before
    public void setup() {
        Assume.assumeTrue(dataSource != null);
        userService = new UserService(dataSource);
    }

    @Test
    public void createUser() throws Exception {
        User user = userService.createUser("jack", "test123", UserService.USER_ROLE_CUSTOMER);
        System.out.println("user created");
    }

    @Test
    public void updatePassword() throws Exception {
        userService.updatePassword(1, "test100", "test123");
    }
}
