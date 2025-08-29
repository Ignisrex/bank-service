package com.bankservice.controller;

import com.bankservice.dto.SignupRequest;
import com.bankservice.dto.LoginRequest;
import com.bankservice.dto.AccountDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureTestMockMvc
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:sqlite::memory:",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testSignup() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("Test User");
        signupRequest.setEmail("test@example.com");
        signupRequest.setPassword("password123");
        
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setType("CHECKING");
        accountDTO.setPrimaryFlag(true);
        accountDTO.setBalance(1000.0);
        signupRequest.setAccounts(List.of(accountDTO));

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.name").value("Test User"))
                .andExpect(jsonPath("$.user.email").value("test@example.com"));
    }

    @Test
    public void testLogin() throws Exception {
        // First create a user
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setName("Login Test User");
        signupRequest.setEmail("login@example.com");
        signupRequest.setPassword("password123");
        
        AccountDTO accountDTO = new AccountDTO();
        accountDTO.setType("CHECKING");
        accountDTO.setPrimaryFlag(true);
        accountDTO.setBalance(1000.0);
        signupRequest.setAccounts(List.of(accountDTO));

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());

        // Then test login
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("login@example.com");
        loginRequest.setPassword("password123");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.user.email").value("login@example.com"));
    }

    @Test
    public void testCardValidation() throws Exception {
        String cardValidationJson = """
        {
            "cardNumber": "1234567890123456",
            "cvv": "123"
        }
        """;

        mockMvc.perform(post("/auth/card/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cardValidationJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    public void testInvalidLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail("nonexistent@example.com");
        loginRequest.setPassword("wrongpassword");

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    public void testInvalidCardValidation() throws Exception {
        String cardValidationJson = """
        {
            "cardNumber": "0000000000000000",
            "cvv": "000"
        }
        """;

        mockMvc.perform(post("/auth/card/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cardValidationJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }
}
