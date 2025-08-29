package com.bankservice;

import com.bankservice.dto.SignupRequest;
import com.bankservice.dto.LoginRequest;
import com.bankservice.dto.AccountDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:sqlite::memory:",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
public class BankServiceApplicationTests {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void testHealthEndpoint() {
        ResponseEntity<Map> response = restTemplate.getForEntity(
            "http://localhost:" + port + "/actuator/health", 
            Map.class
        );
        
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("UP", response.getBody().get("status"));
    }

    @Test
    public void testAuthSignup() {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("Test User");
        signupRequest.setEmail("unique.test@example.com");
        signupRequest.setPassword("password123");
        
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setType("CHECKING");
        accountDTO.setPrimaryFlag(true);
        accountDTO.setBalance(1000.0);
        signupRequest.setAccounts(List.of(accountDTO));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SignupRequest> request = new HttpEntity<>(signupRequest, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/auth/signup", 
            request, 
            String.class
        );

        System.out.println("Response Status: " + response.getStatusCode());
        System.out.println("Response Body: " + response.getBody());

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("token"));
        assertTrue(response.getBody().contains("unique.test@example.com"));
    }

    @Test
    public void testAuthLogin() {
        // First create a user
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("Login Test User");
        signupRequest.setEmail("unique.login@example.com");
        signupRequest.setPassword("password123");
        
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setType("CHECKING");
        accountDTO.setPrimaryFlag(true);
        accountDTO.setBalance(1000.0);
        signupRequest.setAccounts(List.of(accountDTO));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<SignupRequest> signupRequestEntity = new HttpEntity<>(signupRequest, headers);

        restTemplate.postForEntity(
            "http://localhost:" + port + "/auth/signup", 
            signupRequestEntity, 
            String.class
        );

        // Then test login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("unique.login@example.com");
        loginRequest.setPassword("password123");

        HttpEntity<LoginRequest> loginRequestEntity = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/auth/login", 
            loginRequestEntity, 
            String.class
        );

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("token"));
        assertTrue(response.getBody().contains("unique.login@example.com"));
    }

    @Test
    public void testCardValidation() {
        String cardValidationJson = """
        {
            "cardNumber": "1234567890123456",
            "cvv": "123"
        }
        """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(cardValidationJson, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/auth/card/validate", 
            request, 
            Map.class
        );

        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("token"));
    }

    @Test
    public void testInvalidLogin() {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("wrongpassword");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LoginRequest> request = new HttpEntity<>(loginRequest, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/auth/login", 
            request, 
            Map.class
        );

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
    }

    @Test
    public void testInvalidCardValidation() {
        String cardValidationJson = """
        {
            "cardNumber": "0000000000000000",
            "cvv": "000"
        }
        """;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(cardValidationJson, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(
            "http://localhost:" + port + "/auth/card/validate", 
            request, 
            Map.class
        );

        assertEquals(400, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey("error"));
    }
}
