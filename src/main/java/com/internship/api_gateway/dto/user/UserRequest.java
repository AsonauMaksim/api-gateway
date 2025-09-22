package com.internship.api_gateway.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserRequest {
    private String name;
    private String surname;
    private LocalDate birthDate;
    private String email;
}
