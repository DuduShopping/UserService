package com.dudu.users;

import com.dudu.OAuthFilter;
import com.dudu.common.LoggedUser;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping(path = "/greeting")
    public String greeting() {
        return "hello, this is user service";
    }

    @RequestMapping(path = "/greeting/logged")
    public String secureGreeting(@RequestAttribute(name=OAuthFilter.LOGGED_USER) LoggedUser user) {
        return "hello, UserId " + user.getUserId();
    }
}
