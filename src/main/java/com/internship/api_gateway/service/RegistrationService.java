package com.internship.api_gateway.service;

import com.internship.api_gateway.dto.gateway.RegistrationRequest;
import com.internship.api_gateway.dto.gateway.RegistrationResponse;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Mono;

public interface RegistrationService {

    Mono<ResponseEntity<RegistrationResponse>> register (RegistrationRequest request);
}
