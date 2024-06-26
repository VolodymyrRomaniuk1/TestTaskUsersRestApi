package org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
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
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.resource.UserResource;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.service.UserService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    public ResponseEntity<UserResource> createUser(@Valid @RequestBody UserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided User has errors: " + getBindingResultErrorMessages(result));
        }
        User createdUser = userService.createUser(UserMapper.INSTANCE.toUser(userDto));
        UserResource userResource = new UserResource(createdUser);
        Link selfLink = linkTo(methodOn(UserController.class).getUser(createdUser.getId())).withSelfRel();
        userResource.add(selfLink);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResource);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResource> getUser(@PathVariable long id) {
        User user = userService.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        UserResource userResource = new UserResource(user);
        Link selfLink = linkTo(UserController.class).slash(id).withSelfRel();
        userResource.add(selfLink);
        return ResponseEntity.ok(userResource);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResource> updateUser(@PathVariable long id, @Valid @RequestBody UserDto userDto, BindingResult result) {
        if (result.hasErrors()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Provided User has errors: " + getBindingResultErrorMessages(result));
        }
        User updatedUser = userService.updateUserById(id, UserMapper.INSTANCE.toUser(userDto));
        UserResource userResource = new UserResource(updatedUser);
        Link selfLink = linkTo(methodOn(UserController.class).getUser(id)).withSelfRel();
        userResource.add(selfLink);
        return ResponseEntity.ok(userResource);
    }

    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    public ResponseEntity<UserResource> updateUser(@PathVariable long id, @RequestBody JsonPatch patch) throws JsonPatchException, JsonProcessingException {
        User user = userService.findUserById(id)
                .orElseThrow(() -> new UserNotFoundException("User with id " + id + " not found"));
        User patchedUser = applyPatchToUser(patch, user);
        userService.updateUserById(id, patchedUser);
        UserResource userResource = new UserResource(patchedUser);
        Link selfLink = linkTo(methodOn(UserController.class).getUser(id)).withSelfRel();
        userResource.add(selfLink);
        return ResponseEntity.ok(userResource);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable long id) {
        userService.deleteUserById(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<CollectionModel<UserResource>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        List<UserResource> userResources = users.stream()
                .map(user -> {
                    UserResource userResource = new UserResource(user);
                    Link selfLink = linkTo(methodOn(UserController.class).getUser(user.getId())).withSelfRel();
                    userResource.add(selfLink);
                    return userResource;
                })
                .toList();
        Link link = linkTo(UserController.class).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(userResources, link));
    }

    @GetMapping("/search")
    public ResponseEntity<CollectionModel<UserResource>> getUsersByBirthDateRange(
            @RequestParam @Valid @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @Valid @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<User> foundUsers = userService.getUsersByBirthDateRange(from, to);
        List<UserResource> userResources = foundUsers.stream()
                .map(user -> {
                    UserResource userResource = new UserResource(user);
                    Link selfLink = linkTo(methodOn(UserController.class).getUser(user.getId())).withSelfRel();
                    userResource.add(selfLink);
                    return userResource;
                })
                .toList();

        Link link = linkTo(methodOn(UserController.class).getUsersByBirthDateRange(from, to)).withSelfRel();
        return ResponseEntity.ok(CollectionModel.of(userResources, link));
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
