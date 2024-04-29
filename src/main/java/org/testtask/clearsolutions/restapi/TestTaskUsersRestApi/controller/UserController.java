package org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.dto.UserDto;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.dto.UserMapper;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.exception.UserNotFoundException;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.model.User;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Autowired
    public UserController(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided User has errors: " + getBindingResultErrorMessages(result));
        }
        User createdUser = userService.createUser(UserMapper.INSTANCE.toUser(userDto));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> readUser(@PathVariable long id) {
        User user = userService.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity<User> updateUser(@PathVariable long id, @Valid @RequestBody UserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided User has errors: " + getBindingResultErrorMessages(result));
        }
        User updatedUser = userService.updateUserById(id, UserMapper.INSTANCE.toUser(userDto));
        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<User> updateUser(@PathVariable long id, @RequestBody JsonPatch patch) throws JsonPatchException, JsonProcessingException {
        User user = userService.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        User patchedUser = applyPatchToUser(patch, user);
        userService.updateUserById(id, patchedUser);
        return ResponseEntity.ok(patchedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok().build();
    }

//    @GetMapping("/{email}")
//    public ResponseEntity<User> getUserByEmail(@PathVariable String email) {
//        User user = userService.getUserByEmail(email)
//                .orElseThrow(() -> new UserNotFoundException("User with email " + email + " not found"));
//        return ResponseEntity.ok(user);
//    }
//
//    @PutMapping("/{email}")
//    public ResponseEntity<User> updateUser(@PathVariable String email, @Valid @RequestBody User user) {
//        User updatedUser = userService.updateUserByEmail(email, user);
//        return ResponseEntity.ok(updatedUser);
//    }

//    @DeleteMapping("/{email}")
//    public ResponseEntity<Void> deleteUser(@PathVariable String email) {
//        userService.deleteUserByEmail(email);
//        return ResponseEntity.ok().build();
//    }

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> getUsersByBirthDateRange(
            @RequestParam @Valid @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @Valid @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<User> foundUsers = userService.getUsersByBirthDateRange(from, to);
        return ResponseEntity.ok(foundUsers);
    }

    private List<String> getBindingResultErrorMessages(BindingResult result) {
        List<String> errorMessages = new ArrayList<>();
        for (FieldError error : result.getFieldErrors()) {
            errorMessages.add(error.getDefaultMessage());
        }
        return errorMessages;
    }

    private User applyPatchToUser(JsonPatch patch, User targetUser) throws JsonPatchException, JsonProcessingException {
        JsonNode patched = patch.apply(objectMapper.convertValue(targetUser, JsonNode.class));
        return objectMapper.treeToValue(patched, User.class);
    }
}
