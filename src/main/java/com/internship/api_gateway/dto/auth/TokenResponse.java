package com.internship.api_gateway.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {

    private String accessToken;
    private String refreshToken;
}
