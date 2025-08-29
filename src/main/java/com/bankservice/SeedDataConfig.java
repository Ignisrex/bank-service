package com.bankservice;

import com.bankservice.dto.*;
import com.bankservice.service.AuthService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SeedDataConfig {
    @Bean
    CommandLineRunner init(AuthService authService) {
        return args -> {
            try {
                SignupRequest signup = new SignupRequest();
                signup.setName("Test User");
                signup.setEmail("test@example.com");
                signup.setPassword("password123");
                AccountDTO acc = new AccountDTO();
                acc.setType("CHECKING");
                acc.setPrimaryFlag(true);
                acc.setBalance(1000.0);
                signup.setAccounts(List.of(acc));
                authService.signup(signup);
                System.out.println("Seed data created successfully");
            } catch (Exception e) {
                System.out.println("Seed data already exists or error: " + e.getMessage());
            }
        };
    }
}
