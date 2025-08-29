package com.bankservice;

import com.bankservice.dto.*;
import com.bankservice.model.*;
import com.bankservice.service.AccountService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.List;

@Configuration
public class SeedDataConfig {
    @Bean
    CommandLineRunner init(AccountService accountService) {
        return args -> {
            SignupRequest signup = new SignupRequest();
            signup.setName("Test User");
            AccountDTO acc = new AccountDTO();
            acc.setType("DEBIT");
            acc.setPrimaryFlag(true);
            acc.setBalance(1000.0);
            signup.setAccounts(List.of(acc));
            accountService.signup(signup);
        };
    }
}
