package com.internship.api_gateway.service;

//import com.internship.api_gateway.dto.auth.AuthRequest;
//import com.internship.api_gateway.dto.auth.TokenResponse;
//import com.internship.api_gateway.dto.gateway.RegistrationRequest;
//import com.internship.api_gateway.dto.gateway.RegistrationResponse;
//import com.internship.api_gateway.dto.user.UserRequest;
//import com.internship.api_gateway.exception.AlreadyExistsException;
//import com.internship.api_gateway.exception.RegistrationException;
//import lombok.RequiredArgsConstructor;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;
//
//@Service
//@RequiredArgsConstructor
//public class RegistrationServiceImpl implements RegistrationService {
//
//    private final WebClient.Builder webClientBuilder;
//
//    @Value("${services.auth-service.url:http://localhost:8082}")
//    private String authServiceUrl;
//
//    @Value("${services.user-service.url:http://localhost:8081}")
//    private String userServiceUrl;
//
//    @Override
//    public Mono<ResponseEntity<RegistrationResponse>> register(RegistrationRequest request) {
//        return webClientBuilder.build()
//                .post()
//                .uri(authServiceUrl + "/api/auth/register")
//                .bodyValue(new AuthRequest(request.getUsername(), request.getPassword()))
//                .retrieve()
//                .bodyToMono(TokenResponse.class)
//                .flatMap(tokenResponse ->
//                        webClientBuilder.build()
//                                .post()
//                                .uri(userServiceUrl + "/api/users")
//                                .header("Authorization", "Bearer " + tokenResponse.getAccessToken())
//                                .bodyValue(new UserRequest(
//                                        request.getName(),
//                                        request.getSurname(),
//                                        request.getBirthDate(),
//                                        request.getEmail()
//                                ))
//                                .retrieve()
//                                .bodyToMono(RegistrationResponse.class)
//                                .map(userResponse -> ResponseEntity.status(201).body(userResponse))
//                                .onErrorResume(userEx ->
//                                        // Rollback
//                                        webClientBuilder.build()
//                                                .delete()
//                                                .uri(authServiceUrl + "/api/auth/delete-by-username?username=" + request.getUsername())
//                                                .header("Authorization", "Bearer " + tokenResponse.getAccessToken())
//                                                .retrieve()
//                                                .toBodilessEntity()
//                                                .then(Mono.error(new RegistrationException("User service registration failed. Rollback performed.")))
//                                )
//                )
//                .onErrorResume(authEx -> {
//                    String message = authEx.getMessage() != null ? authEx.getMessage() : "Auth service registration failed.";
//                    if (message.contains("already exists") || message.contains("409")) {
//                        return Mono.error(new AlreadyExistsException("User with this username already exists"));
//                    }
//                    return Mono.error(new RegistrationException(message));
//                });
//    }
//}
//

import com.internship.api_gateway.dto.auth.AuthRequest;
import com.internship.api_gateway.dto.auth.TokenResponse;
import com.internship.api_gateway.dto.gateway.RegistrationRequest;
import com.internship.api_gateway.dto.gateway.RegistrationResponse;
import com.internship.api_gateway.dto.user.UserRequest;
import com.internship.api_gateway.exception.AlreadyExistsException;
import com.internship.api_gateway.exception.RegistrationException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService {

    private final WebClient.Builder webClientBuilder;

    @Value("${services.auth-service.url:http://localhost:8082}")
    private String authServiceUrl;

    @Value("${services.user-service.url:http://localhost:8081}")
    private String userServiceUrl;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private SecretKey jwtKey;

    @PostConstruct
    void init() {
        this.jwtKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret));
    }

    private String extractUserId(String accessToken) {
        return Jwts.parser()
                .verifyWith(jwtKey)
                .build()
                .parseSignedClaims(accessToken)
                .getPayload()
                .getSubject(); // это id учётки из auth-service
    }

    @Override
    public Mono<ResponseEntity<RegistrationResponse>> register(RegistrationRequest request) {
        return webClientBuilder.build()
                .post()
                .uri(authServiceUrl + "/api/auth/register")
                .bodyValue(new AuthRequest(request.getUsername(), request.getPassword()))
                .retrieve()
                .bodyToMono(TokenResponse.class)
                .flatMap(tokens -> {
                    String accessToken = tokens.getAccessToken();
                    String userId = extractUserId(accessToken);

                    return webClientBuilder.build()
                            .post()
                            .uri(userServiceUrl + "/api/users")
                            .header("Authorization", "Bearer " + accessToken)
                            .header("X-User-Id", userId) // <- ключевая строка
                            .bodyValue(new UserRequest(
                                    request.getName(),
                                    request.getSurname(),
                                    request.getBirthDate(),
                                    request.getEmail()
                            ))
                            .retrieve()
                            .bodyToMono(RegistrationResponse.class)
                            .map(userResponse -> ResponseEntity.status(201).body(userResponse))
                            .onErrorResume(userEx ->
                                    // Rollback в auth-service если создание пользователя не удалось
                                    webClientBuilder.build()
                                            .delete()
                                            .uri(authServiceUrl + "/api/auth/delete-by-username?username=" + request.getUsername())
                                            .header("Authorization", "Bearer " + accessToken)
                                            .retrieve()
                                            .toBodilessEntity()
                                            .then(Mono.error(new RegistrationException("User service registration failed. Rollback performed.")))
                            );
                })
                .onErrorResume(authEx -> {
                    String message = authEx.getMessage() != null ? authEx.getMessage() : "Auth service registration failed.";
                    if (message.contains("already exists") || message.contains("409")) {
                        return Mono.error(new AlreadyExistsException("User with this username already exists"));
                    }
                    return Mono.error(new RegistrationException(message));
                });
    }
}

