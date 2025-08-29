package com.bankservice.controller;

import com.bankservice.config.BaseTestConfiguration;
import com.bankservice.dto.AccountDTO;
import com.bankservice.dto.SignupRequest;
import com.bankservice.dto.TransferRequest;
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

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for TransferController endpoints.
 * Tests money transfer functionality between accounts.
 */
public class TransferControllerTest extends BaseTestConfiguration {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private HttpHeaders headers;
    private String authToken;
    private Long fromAccountId;
    private Long toAccountId;

    @BeforeEach
    void setUp() throws Exception {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        
        // Create a user with multiple accounts and get auth token
        setupUserAndAccounts();
        headers.setBearerAuth(authToken);
    }

    @Test
    void testTransferMoney_Success() throws Exception {
        // Given
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromAccountId(fromAccountId);
        transferRequest.setToAccountId(toAccountId);
        transferRequest.setAmount(new BigDecimal("100.00"));

        HttpEntity<TransferRequest> request = new HttpEntity<>(transferRequest, headers);

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            buildUrl(port, "/api/transfer"),
            request,
            String.class
        );

        // Then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        
        JsonNode responseJson = objectMapper.readTree(response.getBody());
        assertTrue(responseJson.has("message"));
        assertTrue(responseJson.has("transaction"));
        
        JsonNode transaction = responseJson.get("transaction");
        assertTrue(transaction.has("id"));
        assertTrue(transaction.has("amount"));
        assertTrue(transaction.has("timestamp"));
        assertEquals(new BigDecimal("100.00"), new BigDecimal(transaction.get("amount").asText()));
    }

    @Test
    void testTransferMoney_InsufficientFunds() throws Exception {
        // Given - Transfer more than account balance
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromAccountId(fromAccountId);
        transferRequest.setToAccountId(toAccountId);
        transferRequest.setAmount(new BigDecimal("2000.00")); // More than initial balance

        HttpEntity<TransferRequest> request = new HttpEntity<>(transferRequest, headers);

        // When
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.postForEntity(
            buildUrl(port, "/api/transfer"),
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
    void testTransferMoney_SameAccount() throws Exception {
        // Given - Transfer to same account
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromAccountId(fromAccountId);
        transferRequest.setToAccountId(fromAccountId); // Same account
        transferRequest.setAmount(new BigDecimal("100.00"));

        HttpEntity<TransferRequest> request = new HttpEntity<>(transferRequest, headers);

        // When
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.postForEntity(
            buildUrl(port, "/api/transfer"),
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
    void testTransferMoney_NegativeAmount() throws Exception {
        // Given
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromAccountId(fromAccountId);
        transferRequest.setToAccountId(toAccountId);
        transferRequest.setAmount(new BigDecimal("-100.00")); // Negative amount

        HttpEntity<TransferRequest> request = new HttpEntity<>(transferRequest, headers);

        // When
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.postForEntity(
            buildUrl(port, "/api/transfer"),
            request,
            Map.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    void testTransferMoney_NonexistentAccount() throws Exception {
        // Given
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromAccountId(999L); // Non-existent account
        transferRequest.setToAccountId(toAccountId);
        transferRequest.setAmount(new BigDecimal("100.00"));

        HttpEntity<TransferRequest> request = new HttpEntity<>(transferRequest, headers);

        // When
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.postForEntity(
            buildUrl(port, "/api/transfer"),
            request,
            Map.class
        );

        // Then
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        Map<?, ?> responseBody = response.getBody();
        assertNotNull(responseBody);
        assertTrue(responseBody.containsKey("error"));
    }

    @Test
    void testTransferMoney_Unauthorized() {
        // Given - No auth token
        HttpHeaders noAuthHeaders = new HttpHeaders();
        noAuthHeaders.setContentType(MediaType.APPLICATION_JSON);
        
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromAccountId(fromAccountId);
        transferRequest.setToAccountId(toAccountId);
        transferRequest.setAmount(new BigDecimal("100.00"));

        HttpEntity<TransferRequest> request = new HttpEntity<>(transferRequest, noAuthHeaders);

        // When
        ResponseEntity<String> response = restTemplate.postForEntity(
            buildUrl(port, "/api/transfer"),
            request,
            String.class
        );

        // Then
        assertEquals(HttpStatus.FORBIDDEN, response.getStatusCode());
    }

    @Test
    void testTransferMoney_ZeroAmount() throws Exception {
        // Given
        TransferRequest transferRequest = new TransferRequest();
        transferRequest.setFromAccountId(fromAccountId);
        transferRequest.setToAccountId(toAccountId);
        transferRequest.setAmount(BigDecimal.ZERO); // Zero amount

        HttpEntity<TransferRequest> request = new HttpEntity<>(transferRequest, headers);

        // When
        @SuppressWarnings("rawtypes")
        ResponseEntity<Map> response = restTemplate.postForEntity(
            buildUrl(port, "/api/transfer"),
            request,
            Map.class
        );

        // Then
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    /**
     * Helper method to create a user with multiple accounts and set up test data
     */
    private void setupUserAndAccounts() throws Exception {
        // Create user with checking account
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("Transfer Test User");
        signupRequest.setEmail("transfer.test@example.com");
        signupRequest.setPassword("password123");
        
        AccountDTO checkingAccount = new AccountDTO();
        checkingAccount.setType("CHECKING");
        checkingAccount.setPrimaryFlag(true);
        checkingAccount.setBalance(1000.0);
        signupRequest.setAccounts(List.of(checkingAccount));

        HttpHeaders signupHeaders = new HttpHeaders();
        signupHeaders.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SignupRequest> signupEntity = new HttpEntity<>(signupRequest, signupHeaders);

        ResponseEntity<String> signupResponse = restTemplate.postForEntity(
            buildUrl(port, "/auth/signup"), 
            signupEntity, 
            String.class
        );

        assertEquals(HttpStatus.OK, signupResponse.getStatusCode());
        JsonNode signupJson = objectMapper.readTree(signupResponse.getBody());
        authToken = signupJson.get("token").asText();

        // Create a second account
        HttpHeaders authHeaders = new HttpHeaders();
        authHeaders.setContentType(MediaType.APPLICATION_JSON);
        authHeaders.setBearerAuth(authToken);
        
        AccountDTO savingsAccount = new AccountDTO();
        savingsAccount.setType("SAVINGS");
        savingsAccount.setPrimaryFlag(false);
        savingsAccount.setBalance(500.0);

        HttpEntity<AccountDTO> accountEntity = new HttpEntity<>(savingsAccount, authHeaders);
        ResponseEntity<String> accountResponse = restTemplate.postForEntity(
            buildUrl(port, "/api/accounts"),
            accountEntity,
            String.class
        );

        assertEquals(HttpStatus.OK, accountResponse.getStatusCode());

        // Get all accounts to extract IDs
        ResponseEntity<String> accountsResponse = restTemplate.exchange(
            buildUrl(port, "/api/accounts"),
            HttpMethod.GET,
            new HttpEntity<>(authHeaders),
            String.class
        );

        assertEquals(HttpStatus.OK, accountsResponse.getStatusCode());
        JsonNode accountsArray = objectMapper.readTree(accountsResponse.getBody());
        assertTrue(accountsArray.isArray());
        assertTrue(accountsArray.size() >= 2);

        // Store account IDs for transfers
        fromAccountId = accountsArray.get(0).get("id").asLong();
        toAccountId = accountsArray.get(1).get("id").asLong();
    }
}
