package org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.controller.UserController;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.model.User;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
//@AutoConfigureMockMvc
//@SpringBootTest
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    private final String apiUrl = "/api/users";

    private List<User> users = new ArrayList<>();

    @BeforeEach
    public void setup() {
        users.clear();

        users.add(
                User.builder()
                        .id(1L)
                        .email("foomail1@mail.com")
                        .firstName("John")
                        .lastName("Doe")
                        .birthDate(LocalDate.of(2001, 1, 5))
                        .build()
        );
        users.add(
                User.builder()
                        .id(2L)
                        .email("foomail2@mail.com")
                        .firstName("Maria")
                        .lastName("Smith")
                        .birthDate(LocalDate.of(2002, 2, 12))
                        .address("Aroyo Lane 3, LA")
                        .build()
        );
        users.add(
                User.builder()
                        .id(3L)
                        .email("foomail3@mail.com")
                        .firstName("Mark")
                        .lastName("Brown")
                        .birthDate(LocalDate.of(2003, 4, 1))
                        .address("Carbon Boulevard 54A, NY")
                        .phoneNumber("3010394099")
                        .build()
        );
    }

    @Test
    public void testGetAllUsers() throws Exception {
        // Mock service behavior
        when(userService.getAllUsers()).thenReturn(users);

        // Perform GET request to /api/users
        mockMvc.perform(get(apiUrl))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userResourceList.length()").value(users.size()))
                .andDo(print());

        verify(userService).getAllUsers();
    }

}
