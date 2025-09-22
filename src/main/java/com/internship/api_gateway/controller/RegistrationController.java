package com.internship.api_gateway.controller;

import com.internship.api_gateway.dto.gateway.RegistrationRequest;
import com.internship.api_gateway.dto.gateway.RegistrationResponse;
import com.internship.api_gateway.service.RegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RegistrationController {

    private final RegistrationService registrationService;

    @PostMapping("/register")
    public Mono<ResponseEntity<RegistrationResponse>> register(@Valid @RequestBody RegistrationRequest request) {
        return registrationService.register(request);
    }
}
