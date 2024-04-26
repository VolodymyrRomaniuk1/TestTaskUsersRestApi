package org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.service;

import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);
    Optional<User> getUserByEmail(String email);
    User updateUserByEmail(String email, User updatedUser);
    void deleteUserByEmail(String email);
    List<User> getAllUsers();
    List<User> getUsersByBirthDateRange(LocalDate from, LocalDate to);
}
