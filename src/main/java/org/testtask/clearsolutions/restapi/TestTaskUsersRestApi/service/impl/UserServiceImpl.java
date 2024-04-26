package org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.service.impl;

import org.springframework.stereotype.Service;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.model.User;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final List<User> users = new ArrayList<>();

    @Override
    public User createUser(User user) {
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
    public User updateUser(String email, User updatedUser) {
        User user = getUserByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User with email " + email + " not found"));
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
            throw new IllegalArgumentException("User with email " + email + " not found");
        }
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }

    @Override
    public List<User> getUsersByBirthDateRange(LocalDate from, LocalDate to) {
        List<User> foundUsers = new ArrayList<>();
        for (User user : users) {
            if (user.getBirthDate().isAfter(from) && user.getBirthDate().isBefore(to)) {
                foundUsers.add(user);
            }
        }
        return foundUsers;
    }
}
