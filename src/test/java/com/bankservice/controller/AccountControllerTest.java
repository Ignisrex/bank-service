package com.bankservice.controller;

import com.bankservice.config.BaseTestConfiguration;
import com.bankservice.dto.AccountDTO;
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
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for AccountController endpoints.
 * Tests account management functionality including getting accounts and creating new ones.
 */
public class AccountControllerTest extends BaseTestConfiguration {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private HttpHeaders headers;
    private String authToken;

    @BeforeEach
    void setUp() throws Exception {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Create a user and get auth token for protected endpoints
        authToken = createUserAndGetToken();
        headers.setBearerAuth(authToken);
    }

    @Test
    void testGetUserAccounts_Success() throws Exception {
        // When
        ResponseEntity<String> response = restTemplate.exchange(
            buildUrl(port, "/api/accounts"),
            HttpMethod.GET,
            new HttpEntity<>(headers),
            String.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        JsonNode responseArray = objectMapper.readTree(response.getBody());
        assertTrue(responseArray.isArray());
        assertTrue(responseArray.size() > 0);
        
        // Verify account structure
        JsonNode firstAccount = responseArray.get(0);
        assertTrue(firstAccount.has("id"));
        assertTrue(firstAccount.has("type"));
        assertTrue(firstAccount.has("balance"));
        assertTrue(firstAccount.has("primaryFlag"));
    }

    @Test
    void testGetUserAccounts_Unauthorized() {
        // Given - No auth token
        HttpHeaders noAuthHeaders = new HttpHeaders();
        noAuthHeaders.setContentType(MediaType.APPLICATION_JSON);

        // When
        ResponseEntity<String> response = restTemplate.exchange(
            buildUrl(port, "/api/accounts"),
            HttpMethod.GET,
            new HttpEntity<>(noAuthHeaders),
            String.class
        );

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testCreateAccount_Success() throws Exception {
        // Given
        AccountDTO newAccount = new AccountDTO();
        newAccount.setType("SAVINGS");
        newAccount.setPrimaryFlag(false);
        newAccount.setBalance(2000.0);

        HttpEntity<AccountDTO> request = new HttpEntity<>(newAccount, headers);

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            buildUrl(port, "/api/accounts"),
            request,
            String.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        JsonNode responseJson = objectMapper.readTree(response.getBody());
        assertTrue(responseJson.has("id"));
        assertEquals("SAVINGS", responseJson.get("type").asText());
        assertEquals(2000.0, responseJson.get("balance").asDouble());
        assertFalse(responseJson.get("primaryFlag").asBoolean());
    }

    @Test
    void testCreateAccount_InvalidType() {
        // Given
        AccountDTO newAccount = new AccountDTO();
        newAccount.setType("INVALID_TYPE");
        newAccount.setPrimaryFlag(false);
        newAccount.setBalance(1000.0);

        HttpEntity<AccountDTO> request = new HttpEntity<>(newAccount, headers);

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            buildUrl(port, "/api/accounts"),
            request,
            String.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateAccount_NegativeBalance() {
        // Given
        AccountDTO newAccount = new AccountDTO();
        newAccount.setType("CHECKING");
        newAccount.setPrimaryFlag(false);
        newAccount.setBalance(-100.0);

        HttpEntity<AccountDTO> request = new HttpEntity<>(newAccount, headers);

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            buildUrl(port, "/api/accounts"),
            request,
            String.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testCreateAccount_Unauthorized() {
        // Given - No auth token
        HttpHeaders noAuthHeaders = new HttpHeaders();
        noAuthHeaders.setContentType(MediaType.APPLICATION_JSON);
        
        AccountDTO newAccount = new AccountDTO();
        newAccount.setType("SAVINGS");
        newAccount.setPrimaryFlag(false);
        newAccount.setBalance(1000.0);

        HttpEntity<AccountDTO> request = new HttpEntity<>(newAccount, noAuthHeaders);

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            buildUrl(port, "/api/accounts"),
            request,
            String.class
        );

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    /**
     * Helper method to create a user and return the JWT token
     */
    private String createUserAndGetToken() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("Account Test User");
        signupRequest.setEmail("account.test@example.com");
        signupRequest.setPassword("password123");
        
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setType("CHECKING");
        accountDTO.setPrimaryFlag(true);
        accountDTO.setBalance(1000.0);
        signupRequest.setAccounts(List.of(accountDTO));

        HttpHeaders signupHeaders = new HttpHeaders();
        signupHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SignupRequest> request = new HttpEntity<>(signupRequest, signupHeaders);

        ResponseEntity<String> response = restTemplate.postForEntity(
            buildUrl(port, "/auth/signup"), 
            request, 
            String.class
        );

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JsonNode responseJson = objectMapper.readTree(response.getBody());
        return responseJson.get("token").asText();
    }
}
