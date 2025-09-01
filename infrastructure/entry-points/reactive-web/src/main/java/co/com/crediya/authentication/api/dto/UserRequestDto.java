package co.com.crediya.authentication.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDate;

public record UserRequestDto(

        @NotBlank(message = "name is mandatory")
        String name,

        @NotBlank(message = "last name is mandatory")
        String lastName,

        @NotBlank(message = "email is mandatory")
        @Email(message = "email format is not valid", regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
        String email,

        String password,

        String roleId,

        LocalDate birthDate,

        String address,

        String documentId,

        String phoneNumber,

        @NotNull(message = "salary is mandatory")
        @DecimalMin(value = "0.0", inclusive = true, message = "salary must be greater than 0")
        @DecimalMax(value = "15000000.0", inclusive = true, message = "salary must be less than 15000000")
        BigDecimal baseSalary

) {}
