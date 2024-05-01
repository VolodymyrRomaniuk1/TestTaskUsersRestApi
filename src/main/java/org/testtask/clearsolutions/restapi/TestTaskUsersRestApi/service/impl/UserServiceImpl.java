package org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.service.impl;

import jakarta.annotation.PostConstruct;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.exception.UserNotFoundException;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.model.User;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.service.UserService;

import java.time.LocalDate;
import java.util.*;

@Service
public class UserServiceImpl implements UserService {

    private final List<User> users = new ArrayList<>();
    private long nextId = 1;

    @Value("${user.minimum.age}")
    private int minAge;

    private Validator validator;

    @Autowired
    public UserServiceImpl(Validator validator) {
        this.validator = validator;
    }

    @PostConstruct
    private void initialize() {
        createUser(
                User.builder()
                        .email("foomail1@mail.com")
                        .firstName("John")
                        .lastName("Doe")
                        .birthDate(LocalDate.of(2001, 1, 5))
                        .build()
        );
        createUser(
                User.builder()
                        .email("foomail2@mail.com")
                        .firstName("Maria")
                        .lastName("Smith")
                        .birthDate(LocalDate.of(2002, 2, 12))
                        .address("Aroyo Lane 3, LA")
                        .build()
        );
        createUser(
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

    @Override
    public User createUser(User user) {
        if (findUserByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
        }

        validateUser(user);

        user.setId(nextId++);
        users.add(user);
        return user;
    }

    @Override
    public Optional<User> findUserById(long id) {
        return users.stream()
                .filter(user -> user.getId() == id)
                .findFirst();
    }

    @Override
    public User updateUserById(long id, User updatedUser) {
        User user = findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));

        if (findUserByEmail(updatedUser.getEmail()).isPresent()) {
            if (findUserByEmail(updatedUser.getEmail()).get().getId() != id) {
                throw new IllegalArgumentException("User with email " + user.getEmail() + " already exists");
            }
        }

        validateUser(updatedUser);

        user.setEmail(updatedUser.getEmail());
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setBirthDate(updatedUser.getBirthDate());
        user.setAddress(updatedUser.getAddress());
        user.setPhoneNumber(updatedUser.getPhoneNumber());
        return user;
    }

    @Override
    public void deleteUserById(long id) {
        if (!users.removeIf(user -> user.getId() == id)) {
            throw new UserNotFoundException("User with id " + id + " not found");
        }
    }

    @Override
    public Optional<User> findUserByEmail(String email) {
        return users.stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
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

    private void validateUser(User user) {
        if (!isOlderThanMinAge(user.getBirthDate())) {
            throw new IllegalArgumentException("User must be at least " + minAge + " years old");
        }

        Set<ConstraintViolation<User>> violations = validator.validate(user);

        if (!violations.isEmpty()) {
            List<String> errorMessages = violations.stream()
                    .map(violation -> violation.getPropertyPath() + " " + violation.getMessage())
                    .toList();
            throw new IllegalArgumentException("Provided User has errors: " + errorMessages);
        }
    }
}
