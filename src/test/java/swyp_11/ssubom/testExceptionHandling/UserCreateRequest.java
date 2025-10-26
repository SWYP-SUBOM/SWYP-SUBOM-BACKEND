package swyp_11.ssubom.testExceptionHandling;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserCreateRequest(
        @NotBlank(message = "name is required")
        String name,

        @Email(message = "email must be a valid email")
        String email
) {}