package com.bankservice.service;

import com.bankservice.model.*;
import com.bankservice.repository.*;
import com.bankservice.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AuthService {
    
    @Autowired
    private AccountHolderRepository accountHolderRepo;
    
    @Autowired
    private AccountRepository accountRepo;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AccountHolder signup(SignupRequest request) {
        // Check if email already exists
        if (accountHolderRepo.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        
        // Create account holder
        AccountHolder holder = AccountHolder.builder()
            .name(request.getName())
            .email(request.getEmail())
            .password(passwordEncoder.encode(request.getPassword()))
            .build();
        
        // Save holder first to get ID
        holder = accountHolderRepo.save(holder);
        
        // Create default account if none specified
        if (request.getAccounts() == null || request.getAccounts().isEmpty()) {
            Account defaultAccount = Account.builder()
                .type("CHECKING")
                .number("ACC" + System.currentTimeMillis())
                .primaryFlag(true)
                .balance(0.0)
                .holder(holder)
                .build();
            accountRepo.save(defaultAccount);
        } else {
            // Create specified accounts
            List<Account> accounts = new ArrayList<>();
            for (AccountDTO dto : request.getAccounts()) {
                Account acc = Account.builder()
                    .type(dto.getType())
                    .number("ACC" + System.currentTimeMillis() + accounts.size())
                    .primaryFlag(dto.isPrimaryFlag())
                    .balance(dto.getBalance())
                    .holder(holder)
                    .build();
                accounts.add(acc);
            }
            accountRepo.saveAll(accounts);
        }
        
        return holder;
    }

    public AccountHolder authenticate(String email, String password) {
        AccountHolder holder = accountHolderRepo.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!passwordEncoder.matches(password, holder.getPassword())) {
            throw new RuntimeException("Invalid password");
        }
        
        return holder;
    }
}
