package com.internship.api_gateway.dto.gateway;

import com.internship.api_gateway.dto.user.CardInfoResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistrationResponse {

    private Long id;
    private String name;
    private String surname;
    private String email;
    private LocalDate birthDate;
    private List<CardInfoResponse> cards;
}
