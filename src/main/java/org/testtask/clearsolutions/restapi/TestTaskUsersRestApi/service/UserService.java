package org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.service;

import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserService {
    User createUser(User user);

    Optional<User> getUserByEmail(String email);

    Optional<User> findUserById(long id);

//    User updateUserByEmail(String email, User updatedUser);
    User updateUserById(long id, User updatedUser);

//    User patchUserByEmail(String email, Map<String, Object> updates);
//    void deleteUserByEmail(String email);
    void deleteUserById(long id);

    List<User> getAllUsers();

    List<User> getUsersByBirthDateRange(LocalDate from, LocalDate to);
}
