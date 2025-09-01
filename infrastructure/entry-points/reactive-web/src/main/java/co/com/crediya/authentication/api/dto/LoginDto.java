package co.com.crediya.authentication.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginDto(

        @NotBlank(message = "Please enter email address")
        @Email(message = "email format is not valid", regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        String email,

        @NotBlank(message = "Please enter password")
        String password

) {}
