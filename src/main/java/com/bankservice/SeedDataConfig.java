package com.bankservice;

import com.bankservice.dto.*;
import com.bankservice.service.AuthService;
import com.bankservice.repository.AccountHolderRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SeedDataConfig {
    @Bean
    CommandLineRunner init(AuthService authService, AccountHolderRepository accountHolderRepo) {
        return args -> {
            try {
                // Check if test user already exists
                if (accountHolderRepo.findByEmail("test@example.com").isEmpty()) {
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
                } else {
                    System.out.println("Seed data already exists - skipping creation");
                }
            } catch (Exception e) {
                System.out.println("Error creating seed data: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}
