package com.dudu.users;

import com.dudu.database.DataSourceConfiguration;
import com.dudu.database.UserServiceDataSource;
import com.dudu.oauth.OAuthConfiguration;
import com.dudu.oauth.TokenIssuer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(value = SpringRunner.class)
@ContextConfiguration(classes = {DataSourceConfiguration.class, OAuthConfiguration.class})
@TestPropertySource(value = "/test.properties")
public class AuthenticationServiceTest {

    @Autowired
    private UserServiceDataSource dataSource;

    @Autowired
    private TokenIssuer tokenIssuer;

    private AuthenticationService service;

    @Before
    public void setup() {
        service = new AuthenticationService(tokenIssuer, dataSource);
    }

    @Test
    public void login() throws Exception {
        var rsp = service.login("jack", "test123");
        System.out.println(rsp);
    }

}
