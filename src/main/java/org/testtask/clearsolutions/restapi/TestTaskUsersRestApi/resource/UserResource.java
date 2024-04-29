package org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.resource;

import org.springframework.hateoas.RepresentationModel;
import org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.model.User;

public class UserResource extends RepresentationModel<UserResource> {

    private final User user;

    public UserResource(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
