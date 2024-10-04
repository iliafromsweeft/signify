package com.example.demo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final RestTemplate restTemplate;

    @Value("${openapi.client-id}")
    private String clientId;

    @Value("${openapi.client-secret}")
    private String clientSecret;

    @Value("${openapi.auth-url}")
    private String authUrl;

    private String token;
    private LocalDateTime tokenExpirationTime;

    public String authenticate() {
        // Check if token exists and has more than 5 minutes remaining
        if (token != null && LocalDateTime.now().isBefore(tokenExpirationTime.minusMinutes(5))) {
            return token;
        }

        // Prepare headers and body for authentication request
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Language", "en");
        headers.set("Content-Type", "application/json");

        Map<String, String> body = Map.of(
                "clientId", clientId,
                "clientSecret", clientSecret
        );

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        // Send the request
        ResponseEntity<Map> response = restTemplate.exchange(authUrl, HttpMethod.POST, request, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            Map<String, Object> responseBody = response.getBody();
            token = (String) responseBody.get("accessToken");
            Long secondsBeforeExpiration = ((Number) responseBody.get("secondsBeforeTokenExpiration")).longValue();
            tokenExpirationTime = LocalDateTime.now().plusSeconds(secondsBeforeExpiration);
        } else {
            throw new RuntimeException("Failed to authenticate with OpenAPI.");
        }

        return token;
    }

    public void clearToken() {
        this.token = null;
        this.tokenExpirationTime = null;
    }
}
