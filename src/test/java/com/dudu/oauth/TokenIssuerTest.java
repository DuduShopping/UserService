package com.dudu.oauth;

import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;


@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {OAuthConfiguration.class})
@TestPropertySource("/test.properties")
public class TokenIssuerTest {

    @Autowired
    TokenIssuer issuer;

    public void setup() {
        Assume.assumeNotNull(issuer);
    }

    @Test
    public void issue() {
        String token = issuer.issue(1, Arrays.asList("Customer"));
        println(token);
    }

    void println(Object o) {
        System.out.println(o.toString());
        //eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJVc2VySWQiOjEsIlNjb3BlcyI6IkN1c3RvbWVyIiwiaXNzIjoiZHVkdSIsImV4cCI6MTUzMjIyNDQwNSwiaWF0IjoxNTMyMjIwODA1LCJqdGkiOjY2NTk0MjU2ODk0NDMxNjY4OTB9.UL+RR7R/1AzXq8forQVhOYRnO9RSD7cTRp0x4M8d3GbXm8Qi2TxPfqqEBwoncmYOb0H66IIHGUvg+byjQjX4jL6mhsAtMz974HkLmRDQIvR3f49CXTNrH9So0Z4Sj+JI9m6cCxgV5isDwYpms5/p7PXtUKBKX+Pmyc7EiFAPp8pRkIjQA7H2mmVo79V2mmzm6fpyYWQ4t5zmSSObf+sDxPY2wkOc0etm8mZsCmCLhNfWw8Weoc1c+OQ5eqDzYs0E+MYUt/kXF1GmsYzmpJ6m06W4oUFHJBN/eNhZ51M5Q8VER7JK3Jj3C7UYX09FQ4Sod+4Rgf//h+7F2/2Aj14h0g==
    }
}
