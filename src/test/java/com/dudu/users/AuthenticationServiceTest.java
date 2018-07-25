package com.dudu.users;

import com.dudu.database.DataSourceConfiguration;
import com.dudu.database.UserServiceDataSource;
import com.dudu.oauth.OAuthConfiguration;
import com.dudu.oauth.TokenDecoder;
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

    @Autowired
    private TokenDecoder tokenDecoder;

    private AuthenticationService service;

    @Before
    public void setup() {
        service = new AuthenticationService(tokenIssuer, tokenDecoder, dataSource);
    }

    @Test
    public void login() throws Exception {
        var rsp = service.login("jack", "test123");
        System.out.println(rsp);
    }

    @Test
    public void refreshToken() throws Exception {
        var token = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJVc2VySWQiOjEsIlNjb3BlcyI6ImN1c3RvbWVyIiwiaXNzIjoiZHVkdSIsImV4cCI6MTUzMjQ4NjU4MywiaWF0IjoxNTMyNDgyOTgzLCJqdGkiOjc2Njg0MzYyMzAxNTg4MzE2NDR9.fM5hivCZJ43jBDr9wCpKWv+CaLtGHAAChGl9bg7sRaBFsxDNKKP7/yAndhwBUgkvjMDtg3IyT6hrvfTzd/ttgXPkg9A1UpMZs+KAyAu+gAy1O3TtT4KLFa9gWuHLI8NN+CwhrHiCt1sAnfYwoKK1rUYqyKcR8p1DKmV+NBYBRBHJEq33b6Ox4bbRhF64rtQKcGhL+gTyUglKM7TjuYwGT6AIr9ob9h2N+bmj1Ol140LhCuI3ezAgZbxq1gmNmi5CZHQrw9CIlgQKrlUGrDi7gC3jFux1QS1mh+b92PmflDY4H0pM9UBqWPf0stO1l7zGy2oRg625d39mmTcv8z+hFA==";
        var newToken = service.refreshToken(token);
        System.out.println(newToken);
    }

}
