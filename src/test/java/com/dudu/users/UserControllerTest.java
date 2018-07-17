package com.dudu.users;

import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(value = SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserController userController;

    @Test
    public void createUser() throws Exception {
        JSONObject requestBody = new JSONObject();
        requestBody.put("login", "jack");
        requestBody.put("password", "test123");
        var mockRequest = MockMvcRequestBuilders
                .post("/user")
                .param("login", "jack")
                .param("password", "123456");

        mvc.perform(mockRequest)
                .andExpect(status().is2xxSuccessful())
                .andDo(print());
    }

    @Test
    public void test() {
        Assert.assertNotNull(userController);
    }
}
