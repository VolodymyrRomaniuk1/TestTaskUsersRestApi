package org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.dto.UserDto;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.exception.UserNotFoundException;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.model.User;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTests {

    @Autowired
    private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    private ObjectWriter objectWriter = objectMapper.writer().withDefaultPrettyPrinter();

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
    @DisplayName("Test getAllUsers()")
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

    @Test
    @DisplayName("Test getUserById()")
    public void testGetUserById() throws Exception {
        long userId = 1L;
        User expectedUser = users.get(0);

        // Mock Service behavior
        when(userService.findUserById(userId)).thenReturn(Optional.ofNullable(expectedUser));

        // Perform GET request to /api/users/{id}
        mockMvc.perform(get(apiUrl + "/{id}", userId).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(userId))
                .andExpect(jsonPath("$.user.email").value(expectedUser.getEmail()))
                .andExpect(jsonPath("$.user.firstName").value(expectedUser.getFirstName()))
                .andExpect(jsonPath("$.user.lastName").value(expectedUser.getLastName()))
                .andExpect(jsonPath("$.user.birthDate").value(expectedUser.getBirthDate().toString()))
                .andExpect(jsonPath("$.user.address").value(Matchers.nullValue()))
                .andExpect(jsonPath("$.user.phoneNumber").value(Matchers.nullValue()))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost" + apiUrl + "/" + userId))
                .andDo(print());

        verify(userService).findUserById(userId);
    }

    @Test
    @DisplayName("Test getUserById() (Not found)")
    public void testGetUserById_NotFound() throws Exception {
        long nonExistingUserId = 10000L;

        // Mock Service behavior to return null
        when(userService.findUserById(nonExistingUserId)).thenThrow(UserNotFoundException.class);

        // Perform GET request to /api/users/{id}
        mockMvc.perform(get(apiUrl + "/{id}", nonExistingUserId))
                .andExpect(status().isNotFound());

        verify(userService).findUserById(nonExistingUserId);
    }

    @Test
    @DisplayName("Test createUser()")
    public void testCreateUser() throws Exception {
        long userId = 4L;
        UserDto userDto = UserDto.builder()
                .email("foomail4@mail.com")
                .firstName("Jonathan")
                .lastName("Brown")
                .birthDate(LocalDate.of(2000, 8, 11))
                .address("Carbon Boulevard 54B, NY")
                .phoneNumber("3016841187")
                .build();

        User createdUser = User.builder()
                .id(userId)
                .email("foomail4@mail.com")
                .firstName("Jonathan")
                .lastName("Brown")
                .birthDate(LocalDate.of(2000, 8, 11))
                .address("Carbon Boulevard 54B, NY")
                .phoneNumber("3016841187")
                .build();

        when(userService.createUser(any(User.class))).thenReturn(createdUser);

        // Perform POST request to /api/users
        mockMvc.perform(post(apiUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(userDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.user.id").value(userId))
                .andExpect(jsonPath("$.user.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.user.firstName").value(userDto.getFirstName()))
                .andExpect(jsonPath("$.user.lastName").value(userDto.getLastName()))
                .andExpect(jsonPath("$.user.birthDate").value(userDto.getBirthDate().toString()))
                .andExpect(jsonPath("$.user.address").value(userDto.getAddress()))
                .andExpect(jsonPath("$.user.phoneNumber").value(userDto.getPhoneNumber()))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost" + apiUrl + "/" + userId))
                .andDo(print());

        verify(userService).createUser(any(User.class));
    }

    @ParameterizedTest
    @MethodSource("invalidUserJson")
    @DisplayName("Test createUser() (Negative scenarios)")
    public void testCreateUser_NegativeScenarios(String invalidUserJson) throws Exception {
        // Perform POST request to /api/users
        mockMvc.perform(post(apiUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUserJson))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("Test updateUser() (PUT)")
    public void testUpdateUserPut() throws Exception {
        long userId = 1L;
        UserDto userDto = UserDto.builder()
                .email("foomail4@mail.com")
                .firstName("Jonathan")
                .lastName("Brown")
                .birthDate(LocalDate.of(2000, 8, 11))
                .address("Carbon Boulevard 54B, NY")
                .phoneNumber("3016841187")
                .build();

        User updatedUser = User.builder()
                .id(userId)
                .email("foomail4@mail.com")
                .firstName("Jonathan")
                .lastName("Brown")
                .birthDate(LocalDate.of(2000, 8, 11))
                .address("Carbon Boulevard 54B, NY")
                .phoneNumber("3016841187")
                .build();

        // Mock Service behavior
        when(userService.updateUserById(eq(updatedUser.getId()), any(User.class))).thenReturn(updatedUser);

        // Perform PUT request to /api/users/{id}
        mockMvc.perform(put(apiUrl + "/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(userId))
                .andExpect(jsonPath("$.user.email").value(userDto.getEmail()))
                .andExpect(jsonPath("$.user.firstName").value(userDto.getFirstName()))
                .andExpect(jsonPath("$.user.lastName").value(userDto.getLastName()))
                .andExpect(jsonPath("$.user.birthDate").value(userDto.getBirthDate().toString()))
                .andExpect(jsonPath("$.user.address").value(userDto.getAddress()))
                .andExpect(jsonPath("$.user.phoneNumber").value(userDto.getPhoneNumber()))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost" + apiUrl + "/" + userId))
                .andDo(print());

        verify(userService).updateUserById(eq(updatedUser.getId()), any(User.class));
    }

    @ParameterizedTest
    @MethodSource("invalidUserJson")
    @DisplayName("Test updateUser() (PUT) (Negative scenarios)")
    public void testUpdateUserPut_NegativeScenarios(String invalidUserJson) throws Exception {
        long userId = 1L;

        // Perform PUT request to /api/users
        mockMvc.perform(put(apiUrl + "/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidUserJson))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("Test updateUser() (PATCH)")
    public void testUpdateUserPatch() throws Exception {
        long userId = 1L;
        User existingUser = users.get(0);

        User patchedUser = User.builder()
                .id(existingUser.getId())
                .email(existingUser.getEmail())
                .firstName("Robert")
                .lastName(existingUser.getLastName())
                .birthDate(existingUser.getBirthDate())
                .address("Caiman St. 3")
                .build();

        // Mock Service behavior
        when(userService.findUserById(userId)).thenReturn(Optional.of(existingUser));
        when(userService.updateUserById(eq(userId), eq(patchedUser))).thenReturn(patchedUser);

        // Perform PATCH request to /api/users/{id}
        mockMvc.perform(patch(apiUrl + "/{id}", userId)
                        .contentType(new MediaType("application", "json-patch+json"))
                        .content("""
                                [
                                    {"op":"replace","path":"/firstName","value":"Robert"},\s
                                    {"op":"add","path":"/address","value": "Caiman St. 3"}
                                ]
                                    """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user.id").value(patchedUser.getId()))
                .andExpect(jsonPath("$.user.email").value(patchedUser.getEmail()))
                .andExpect(jsonPath("$.user.firstName").value(patchedUser.getFirstName()))
                .andExpect(jsonPath("$.user.lastName").value(patchedUser.getLastName()))
                .andExpect(jsonPath("$.user.birthDate").value(patchedUser.getBirthDate().toString()))
                .andExpect(jsonPath("$.user.address").value(patchedUser.getAddress()))
                .andExpect(jsonPath("$._links.self.href").value("http://localhost" + apiUrl + "/" + userId))
                .andDo(print());

        verify(userService).updateUserById(eq(patchedUser.getId()), any(User.class));
    }

    @ParameterizedTest
    @MethodSource("invalidPatchJson")
    @DisplayName("Test updateUser() (PATCH) (Negative scenarios)")
    public void testUpdateUserPatch_NegativeScenarios(String invalidPatchJson) throws Exception {
        long userId = 1L;
        User existingUser = users.get(0);

        when(userService.findUserById(userId)).thenReturn(Optional.of(existingUser));
        when(userService.updateUserById(eq(userId), any(User.class))).thenThrow(IllegalArgumentException.class);

        // Perform PATCH request to /api/users
        mockMvc.perform(patch(apiUrl + "/{id}", userId)
                        .contentType(new MediaType("application", "json-patch+json"))
                        .content(invalidPatchJson))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("Test deleteUser()")
    public void testDeleteUser() throws Exception {
        long userId = 1L;

        // Perform DELETE request to /api/users/{id}
        mockMvc.perform(delete(apiUrl + "/{id}", userId))
                .andExpect(status().isOk());

        verify(userService).deleteUserById(userId);
    }

    @Test
    @DisplayName("Test deleteUser() (Not found)")
    public void testDeleteUser_NotFound() throws Exception {
        long userId = 1L;

        // Mock Service behavior
        doThrow(UserNotFoundException.class).when(userService).deleteUserById(userId);

        // Perform DELETE request to /api/users/{id}
        mockMvc.perform(delete(apiUrl + "/{id}", userId))
                .andExpect(status().isNotFound());

        verify(userService).deleteUserById(userId);
    }

    @Test
    @DisplayName("Test getUsersByBirthDateRange()")
    public void testGetUsersByBirthDateRange() throws Exception {
        LocalDate from = LocalDate.now().minusYears(30);
        LocalDate to = LocalDate.now();

        // Mock Service behavior
        when(userService.getUsersByBirthDateRange(from, to)).thenReturn(users);

        // Perform GET request to /api/users/search with parameters
        mockMvc.perform(get(apiUrl + "/search?from={from}&to={to}", from.toString(), to.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userResourceList.length()").value(users.size()))
                .andDo(print());

        verify(userService).getUsersByBirthDateRange(from, to);
    }

    private static Stream<String> invalidUserJson() {
        return Stream.of(
                """
                        {
                            "email":"invalidEmail",
                            "firstName":"John",
                            "lastName":"Doe",
                            "birthDate":"2000-01-01"
                        }
                        """,
                """
                        {
                            "email":"foomail@mail.com",
                            "firstName":"",
                            "lastName":"Doe",
                            "birthDate":"2000-01-01"
                        }
                        """,
                """
                        {
                            "email":"foomail@mail.com",
                            "firstName":"John",
                            "lastName":"",
                            "birthDate":"2000-01-01"
                        }
                        """,
                """
                        {
                            "email":"foomail@mail.com",
                            "firstName":"John",
                            "lastName":"Doe",
                            "birthDate":"invalidDate"
                        }
                        """
        );
    }

    private static Stream<String> invalidPatchJson() {
        return Stream.of(
                """
                             {"op":"invalidOp","path":"/email","value":"foomail@mail.com"}
                        """,
                """
                             {"op":"replace","path":"/invalidPath","value":"foomail@mail.com"}
                        """
        );
    }
}
