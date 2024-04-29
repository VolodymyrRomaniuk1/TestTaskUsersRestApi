package org.testtask.clearsolutions.restapi.TestTaskUsersRestApi.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserDto {

    @NotBlank(message = "Email is required and cannot be blank")
    @Email(message = "Email must be a valid email")
    private String email;

    @NotBlank(message = "First name is required and cannot be blank")
    private String firstName;

    @NotBlank(message = "Last name is required and cannot be blank")
    private String lastName;

    @NotNull(message = "Birth date is required and cannot be null")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    private String address;

    private String phoneNumber;
}