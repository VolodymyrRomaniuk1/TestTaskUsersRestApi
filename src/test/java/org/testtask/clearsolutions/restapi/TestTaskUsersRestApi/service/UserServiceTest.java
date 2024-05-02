package org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.service;

import jakarta.validation.Validation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.exception.UserNotFoundException;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.model.User;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.service.impl.UserServiceImpl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {

    private UserServiceImpl userService;

    private List<User> initialUsers;

    @BeforeEach
    public void setup() {
        userService = new UserServiceImpl(Validation.buildDefaultValidatorFactory().getValidator());

        initialUsers = new ArrayList<>();
        initialUsers.add(
                User.builder()
                        .id(1L)
                        .email("foomail1@mail.com")
                        .firstName("John")
                        .lastName("Doe")
                        .birthDate(LocalDate.of(2001, 1, 5))
                        .build()
        );
        initialUsers.add(
                User.builder()
                        .id(2L)
                        .email("foomail2@mail.com")
                        .firstName("Maria")
                        .lastName("Smith")
                        .birthDate(LocalDate.of(2002, 2, 12))
                        .address("Aroyo Lane 3, LA")
                        .build()
        );
        initialUsers.add(
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
        userService.setUsers(initialUsers);
    }

    @Test
    void testGetAllUsers() {
        List<User> allUsers = userService.getAllUsers();
        assertEquals(3, allUsers.size());
    }

    @Test
    void testFindUserById() {
        long userId = 1L;
        assertTrue(userService.findUserById(userId).isPresent());
    }

    @Test
    void testFindUserById_NotFound() {
        long userId = 4L;
        assertTrue(userService.findUserById(userId).isEmpty());
    }

    @Test
    void testUpdateUser() {
        User userToUpdate = User.builder()
                .email("newemail@mail.com")
                .firstName("New")
                .lastName("User")
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();

        User updatedUser = userService.updateUserById(1, userToUpdate);
        assertEquals(userToUpdate.getEmail(), updatedUser.getEmail());
        assertEquals(userToUpdate.getFirstName(), updatedUser.getFirstName());
        assertEquals(userToUpdate.getLastName(), updatedUser.getLastName());
        assertEquals(userToUpdate.getBirthDate(), updatedUser.getBirthDate());
    }

    @ParameterizedTest
    @MethodSource("invalidUsers")
    void testCreateUser_NegativeScenarios(User user) {
        assertThrows(IllegalArgumentException.class, () -> userService.createUser(user));
    }

    @ParameterizedTest
    @MethodSource("invalidUsers")
    void testUpdateUser_NegativeScenarios(User user) {
        assertThrows(IllegalArgumentException.class, () -> userService.updateUserById(1, user));
    }

    @Test
    void testUpdateUser_NotFound() {
        assertThrows(UserNotFoundException.class, () -> userService.updateUserById(999, User.builder().build()));
    }

    @Test
    void testDeleteUser() {
        assertDoesNotThrow(() -> userService.deleteUserById(1));
    }

    @Test
    void testDeleteUser_NotFound() {
        assertThrows(UserNotFoundException.class, () -> userService.deleteUserById(999));
    }

    @Test
    void testCreateUser() {
        User newUser = User.builder()
                .email("newemail@mail.com")
                .firstName("New")
                .lastName("User")
                .birthDate(LocalDate.of(2000, 1, 1))
                .build();

        User savedUser = userService.createUser(newUser);
        assertEquals(newUser.getEmail(), savedUser.getEmail());
        assertEquals(newUser.getFirstName(), savedUser.getFirstName());
        assertEquals(newUser.getLastName(), savedUser.getLastName());
        assertEquals(newUser.getBirthDate(), savedUser.getBirthDate());
    }

    @Test
    public void testGetUsersByBirthDateRange() {
        assertEquals(0, userService.getUsersByBirthDateRange(LocalDate.of(1990, 1, 1), LocalDate.of(1991, 1, 1)).size());
        assertEquals(1, userService.getUsersByBirthDateRange(LocalDate.of(2000, 1, 1), LocalDate.of(2002, 1, 1)).size());
        assertEquals(2, userService.getUsersByBirthDateRange(LocalDate.of(2000, 1, 1), LocalDate.of(2003, 1, 1)).size());
        assertEquals(3, userService.getUsersByBirthDateRange(LocalDate.of(1990, 1, 1), LocalDate.of(2005, 1, 1)).size());
    }

    @Test
    public void testGetUsersByBirthDateRange_NegativeScenario() {
        assertThrows(IllegalArgumentException.class, () -> userService.getUsersByBirthDateRange(LocalDate.of(2000, 1, 1), LocalDate.of(1999, 1, 1)).size());
    }

    private static Stream<User> invalidUsers() {
        return Stream.of(
                User.builder()
                        .id(1L)
                        .email("invalidEmail")
                        .firstName("New")
                        .lastName("User")
                        .birthDate(LocalDate.of(2000, 1, 1))
                        .build(),
                User.builder()
                        .id(1L)
                        .email("newemail@mail.com")
                        .firstName("")
                        .lastName("User")
                        .birthDate(LocalDate.of(2000, 1, 1))
                        .build(),
                User.builder()
                        .id(1L)
                        .email("newemail@mail.com")
                        .firstName("New")
                        .lastName("")
                        .birthDate(LocalDate.of(2000, 1, 1))
                        .build(),
                User.builder()
                        .id(1L)
                        .email("newemail@mail.com")
                        .firstName("New")
                        .lastName("User")
                        .birthDate(LocalDate.now())
                        .build()
        );
    }
}
