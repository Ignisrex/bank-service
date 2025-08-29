package com.bankservice.controller;

import com.bankservice.config.BaseTestConfiguration;
import com.bankservice.dto.AccountDTO;
import com.bankservice.dto.LoginRequest;
import com.bankservice.dto.SignupRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for AuthController endpoints.
 * Tests user authentication, registration, and card validation functionality.
 */
public class AuthControllerTest extends BaseTestConfiguration {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
    }

    @Test
    void testUserSignup_Success() throws Exception {
        // Given
        SignupRequest signupRequest = createSignupRequest(
            "Test User", 
            "unique.test@example.com", 
            "password123"
        );

        HttpEntity<SignupRequest> request = new HttpEntity<>(signupRequest, headers);

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            buildUrl(port, "/auth/signup"), 
            request, 
            String.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        JsonNode responseJson = objectMapper.readTree(response.getBody());
        assertTrue(responseJson.has("token"));
        assertTrue(responseJson.has("user"));
        assertEquals("unique.test@example.com", responseJson.get("user").get("email").asText());
    }

    @Test
    void testUserSignup_DuplicateEmail() throws Exception {
        // Given - Create first user
        SignupRequest firstUser = createSignupRequest(
            "First User", 
            "duplicate@example.com", 
            "password123"
        );
        
        HttpEntity<SignupRequest> firstRequest = new HttpEntity<>(firstUser, headers);
        restTemplate.postForEntity(buildUrl(port, "/auth/signup"), firstRequest, String.class);

        // When - Try to create second user with same email
        SignupRequest secondUser = createSignupRequest(
            "Second User", 
            "duplicate@example.com", 
            "password456"
        );
        
        HttpEntity<SignupRequest> secondRequest = new HttpEntity<>(secondUser, headers);
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.postForEntity(
            buildUrl(port, "/auth/signup"), 
            secondRequest, 
            Map.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<?, ?> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
    }

    @Test
    void testUserLogin_Success() throws Exception {
        // Given - Create a user first
        SignupRequest signupRequest = createSignupRequest(
            "Login Test User", 
            "login.test@example.com", 
            "password123"
        );
        
        HttpEntity<SignupRequest> signupEntity = new HttpEntity<>(signupRequest, headers);
        restTemplate.postForEntity(buildUrl(port, "/auth/signup"), signupEntity, String.class);

        // When - Login with created user
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("login.test@example.com");
        loginRequest.setPassword("password123");

        HttpEntity<LoginRequest> loginEntity = new HttpEntity<>(loginRequest, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(
            buildUrl(port, "/auth/login"), 
            loginEntity, 
            String.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        JsonNode responseJson = objectMapper.readTree(response.getBody());
        assertTrue(responseJson.has("token"));
        assertTrue(responseJson.has("user"));
        assertEquals("login.test@example.com", responseJson.get("user").get("email").asText());
    }

    @Test
    void testUserLogin_InvalidCredentials() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("wrongpassword");

        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        // When
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.postForEntity(
            buildUrl(port, "/auth/login"), 
            request, 
            Map.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<?, ?> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
    }

    @Test
    void testUserLogin_MissingEmail() {
        // Given
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setPassword("password123");
        // email is null

        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        // When
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.postForEntity(
            buildUrl(port, "/auth/login"), 
            request, 
            Map.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCardValidation_Success() {
        // Given
        String cardValidationJson = """
        {
            "cardNumber": "1234567890123456",
            "cvv": "123"
        }
        """;

        HttpEntity<String> request = new HttpEntity<>(cardValidationJson, headers);

        // When
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.postForEntity(
            buildUrl(port, "/auth/card/validate"), 
            request, 
            Map.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<?, ?> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("token"));
    }

    @Test
    void testCardValidation_InvalidCard() {
        // Given
        String cardValidationJson = """
        {
            "cardNumber": "0000000000000000",
            "cvv": "000"
        }
        """;

        HttpEntity<String> request = new HttpEntity<>(cardValidationJson, headers);

        // When
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.postForEntity(
            buildUrl(port, "/auth/card/validate"), 
            request, 
            Map.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<?, ?> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
    }

    @Test
    void testCardValidation_MissingCvv() {
        // Given
        String cardValidationJson = """
        {
            "cardNumber": "1234567890123456"
        }
        """;

        HttpEntity<String> request = new HttpEntity<>(cardValidationJson, headers);

        // When
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.postForEntity(
            buildUrl(port, "/auth/card/validate"), 
            request, 
            Map.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Helper method to create a signup request with default account
     */
    private SignupRequest createSignupRequest(String name, String email, String password) {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName(name);
        signupRequest.setEmail(email);
        signupRequest.setPassword(password);
        
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setType("CHECKING");
        accountDTO.setPrimaryFlag(true);
        accountDTO.setBalance(1000.0);
        signupRequest.setAccounts(List.of(accountDTO));
        
        return signupRequest;
    }
}
