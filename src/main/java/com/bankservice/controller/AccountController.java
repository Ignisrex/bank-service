package com.bankservice.controller;

import com.bankservice.dto.*;
import com.bankservice.model.*;
import com.bankservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class AccountController {
    
    @Autowired
    private AccountService accountService;

    // Account Management
    @GetMapping("/accounts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Account>> getAllAccounts() {
        try {
            List<Account> accounts = accountService.getAllAccounts();
            return ResponseEntity.ok(accounts);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/accounts/{accountId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Account> getAccount(@PathVariable Long accountId) {
        try {
            Account account = accountService.getAccountById(accountId);
            return ResponseEntity.ok(account);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/accounts")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> createAccount(@Valid @RequestBody AccountDTO accountDTO) {
        try {
            Account account = accountService.createAccount(accountDTO);
            return ResponseEntity.ok(Map.of(
                "account", account,
                "message", "Account created successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Transaction Operations
    @GetMapping("/accounts/{accountId}/transactions")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Transaction>> getAccountTransactions(@PathVariable Long accountId) {
        try {
            List<Transaction> transactions = accountService.getAccountTransactions(accountId);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/transfer")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> transferMoney(@Valid @RequestBody TransferRequest transferRequest) {
        try {
            Transaction transaction = accountService.transferMoney(transferRequest);
            return ResponseEntity.ok(Map.of(
                "transaction", transaction,
                "message", "Transfer completed successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Card Management
    @GetMapping("/accounts/{accountId}/cards")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<Card>> getAccountCards(@PathVariable Long accountId) {
        try {
            List<Card> cards = accountService.getAccountCards(accountId);
            return ResponseEntity.ok(cards);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/accounts/{accountId}/cards")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> issueCard(@PathVariable Long accountId, @Valid @RequestBody CardRequest cardRequest) {
        try {
            Card card = accountService.issueCard(accountId, cardRequest);
            return ResponseEntity.ok(Map.of(
                "card", card,
                "message", "Card issued successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Statements
    @GetMapping("/accounts/{accountId}/statements")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<?> generateStatement(
            @PathVariable Long accountId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            var statement = accountService.generateStatement(accountId, startDate, endDate);
            return ResponseEntity.ok(Map.of(
                "statement", statement,
                "message", "Statement generated successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
