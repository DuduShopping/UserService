package com.dudu.users;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @RequestMapping(path = "/greeting")
    public String greeting() {
        return "hello, this is user service";
    }
}
