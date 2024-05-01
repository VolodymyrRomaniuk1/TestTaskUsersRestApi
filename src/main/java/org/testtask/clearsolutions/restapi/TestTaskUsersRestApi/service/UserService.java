package org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.service;

import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);

    Optional<User> findUserByEmail(String email);

    Optional<User> findUserById(long id);

    User updateUserById(long id, User updatedUser);

    void deleteUserById(long id);

    List<User> getAllUsers();

    List<User> getUsersByBirthDateRange(LocalDate from, LocalDate to);
}
