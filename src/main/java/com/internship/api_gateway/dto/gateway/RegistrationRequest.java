package com.internship.api_gateway.dto.gateway;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class RegistrationRequest {

    @NotBlank(message = "Username must not be blank")
    @Size(max = 50, message = "Username must be at most 50 characters")
    private String username;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 10, max = 255, message = "Password must be between 10 and 255 characters")
    private String password;

    @NotBlank(message = "Name is required")
    @Size(max = 50)
    private String name;

    @Size(max = 50)
    private String surname;

    @NotNull(message = "Birth date is required")
    @PastOrPresent(message = "Birth date can't be in the future")
    private LocalDate birthDate;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is not valid")
    private String email;
}
