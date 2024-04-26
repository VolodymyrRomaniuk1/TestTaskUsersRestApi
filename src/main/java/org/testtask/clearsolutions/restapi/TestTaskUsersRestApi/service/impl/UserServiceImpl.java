package org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.exception.UserNotFoundException;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.model.User;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final List<User> users = new ArrayList<>();

    {
        users.add(
                User.builder()
                        .email("foomail1@mail.com")
                        .firstName("John")
                        .lastName("Doe")
                        .birthDate(LocalDate.of(2001, 1, 5))
                        .build()
        );
        users.add(
                User.builder()
                        .email("foomail2@mail.com")
                        .firstName("Maria")
                        .lastName("Smith")
                        .birthDate(LocalDate.of(2002, 2, 12))
                        .address("Aroyo Lane 3, LA")
                        .build()
        );
        users.add(
                User.builder()
                        .email("foomail3@mail.com")
                        .firstName("Mark")
                        .lastName("Brown")
                        .birthDate(LocalDate.of(2003, 4, 1))
                        .address("Carbon Boulevard 54A, NY")
                        .phoneNumber("3010394099")
                        .build()
        );
    }

    @Value("${user.minimum.age}")
    private int minAge;

    @Override
    public User createUser(User user) {
        if (!isOlderThanMinAge(user.getBirthDate())) {
            throw new IllegalArgumentException("User must be at least " + minAge + " years old");
        }
        if (getUserByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }
        users.add(user);
        return user;
    }

    @Override
    public Optional<User> getUserByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public User updateUserByEmail(String email, User updatedUser) {
        User user = getUserByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
        if (!isOlderThanMinAge(updatedUser.getBirthDate())) {
            throw new IllegalArgumentException("User must be at least " + minAge + " years old");
        }
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setBirthDate(updatedUser.getBirthDate());
        user.setAddress(updatedUser.getAddress());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        return user;
    }

    @Override
    public void deleteUserByEmail(String email) {
        if (!users.removeIf(user -> user.getEmail().equals(email))) {
            throw new UserNotFoundException("User with email " + email + " not found");
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public List<User> getUsersByBirthDateRange(LocalDate from, LocalDate to) {
        if (from.isAfter(to)) {
            throw new IllegalArgumentException("Birth date range start " + from + " is after range end " + to);
        }
        List<User> foundUsers = new ArrayList<>();
        for (User user : users) {
            if (user.getBirthDate().isAfter(from) && user.getBirthDate().isBefore(to)) {
                foundUsers.add(user);
            }
        }
        return foundUsers;
    }

    private boolean isOlderThanMinAge(LocalDate birthDate) {
        return birthDate.plusYears(minAge).isBefore(LocalDate.now());
    }
}
