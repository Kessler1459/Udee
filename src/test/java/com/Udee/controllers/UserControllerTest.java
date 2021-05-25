package com.Udee.controllers;

import com.Udee.AbstractController;
import com.Udee.models.User;
import com.Udee.services.UserService;
import com.Udee.utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = UserController.class)
public class UserControllerTest extends AbstractController {
    @MockBean
    private UserService userService;
    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    public void findAllUsers() throws Exception {
        //when
        final ResultActions resultActions = givenController().perform(MockMvcRequestBuilders
                .get("/users")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        //then
        assertEquals(HttpStatus.OK.value(),resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void findById() throws Exception {
        //when
        final ResultActions resultActions = givenController().perform(MockMvcRequestBuilders
                .get("/users/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        //then
        assertEquals(HttpStatus.OK.value(),resultActions.andReturn().getResponse().getStatus());
    }

    @Test
    public void addUser() throws Exception {
        //when
        final ResultActions resultActions = givenController().perform(MockMvcRequestBuilders
                .post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtils.aUserJSON()))
                .andExpect(status().isCreated());
        //then
        assertEquals(HttpStatus.OK.value(),resultActions.andReturn().getResponse().getStatus());
    }
}
