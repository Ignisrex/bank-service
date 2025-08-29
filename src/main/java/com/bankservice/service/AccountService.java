package com.bankservice.service;

import com.bankservice.model.*;
import com.bankservice.repository.*;
import com.bankservice.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AccountService {
    @Autowired
    private AccountHolderRepository accountHolderRepo;
    @Autowired
    private AccountRepository accountRepo;
    @Autowired
    private TransactionRepository transactionRepo;
    @Autowired
    private CardRepository cardRepo;

    public AccountHolder signup(SignupRequest request) {
        AccountHolder holder = AccountHolder.builder().name(request.getName()).build();
        List<Account> accounts = new ArrayList<>();
        for (AccountDTO dto : request.getAccounts()) {
            Account acc = Account.builder()
                .type(dto.getType())
                .number("ACC" + System.currentTimeMillis()) // Generate account number
                .primaryFlag(dto.isPrimaryFlag())
                .balance(dto.getBalance())
                .holder(holder)
                .build();
            accounts.add(acc);
        }
        holder.setAccounts(accounts);
        return accountHolderRepo.save(holder);
    }

    // Account Management
    public List<Account> getAllAccounts() {
        return accountRepo.findAll();
    }

    public Account getAccountById(Long accountId) {
        return accountRepo.findById(accountId)
            .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public Account createAccount(AccountDTO accountDTO) {
        // For now, create account for first account holder found
        // In real app, this would use the authenticated user
        AccountHolder holder = accountHolderRepo.findAll().stream()
            .findFirst()
            .orElseThrow(() -> new RuntimeException("No account holder found"));
        
        Account account = Account.builder()
            .type(accountDTO.getType())
            .number("ACC" + System.currentTimeMillis())
            .primaryFlag(accountDTO.isPrimaryFlag())
            .balance(accountDTO.getBalance())
            .holder(holder)
            .build();
        
        return accountRepo.save(account);
    }

    // Transaction Operations
    public List<Transaction> getAccountTransactions(Long accountId) {
        Account account = getAccountById(accountId);
        return transactionRepo.findByAccountOrderByTimestampDesc(account);
    }

    @Transactional
    public Transaction transferMoney(TransferRequest request) {
        Account fromAccount = getAccountById(request.getFromAccountId());
        Account toAccount = getAccountById(request.getToAccountId());
        
        // Check sufficient balance
        if (fromAccount.getBalance() < request.getAmount().doubleValue()) {
            throw new RuntimeException("Insufficient balance");
        }
        
        // Update balances
        fromAccount.setBalance(fromAccount.getBalance() - request.getAmount().doubleValue());
        toAccount.setBalance(toAccount.getBalance() + request.getAmount().doubleValue());
        
        accountRepo.save(fromAccount);
        accountRepo.save(toAccount);
        
        // Create transaction record
        Transaction transaction = Transaction.builder()
            .account(fromAccount)
            .amount(request.getAmount().doubleValue())
            .direction("OUT")
            .description("Transfer to " + toAccount.getNumber() + ": " + request.getDescription())
            .timestamp(LocalDateTime.now())
            .build();
        
        return transactionRepo.save(transaction);
    }

    // Card Management
    public List<Card> getAccountCards(Long accountId) {
        Account account = getAccountById(accountId);
        return cardRepo.findByAccount(account);
    }

    public Card issueCard(Long accountId, CardRequest cardRequest) {
        Account account = getAccountById(accountId);
        
        Card card = Card.builder()
            .account(account)
            .cardNumber(generateCardNumber())
            .cardType(cardRequest.getCardType())
            .cvv(generateCVV())
            .pin(cardRequest.getPin() != null ? cardRequest.getPin() : generatePIN())
            .isActive(true)
            .build();
        
        return cardRepo.save(card);
    }

    // Statement Generation
    public Map<String, Object> generateStatement(Long accountId, String startDate, String endDate) {
        Account account = getAccountById(accountId);
        List<Transaction> transactions;
        
        if (startDate != null && endDate != null) {
            // Filter by date range - simplified for demo
            transactions = getAccountTransactions(accountId);
        } else {
            // Get all transactions
            transactions = getAccountTransactions(accountId);
        }
        
        Map<String, Object> statement = new HashMap<>();
        statement.put("account", account);
        statement.put("transactions", transactions);
        statement.put("generatedAt", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
        statement.put("totalTransactions", transactions.size());
        
        return statement;
    }

    // Helper methods
    private String generateCardNumber() {
        return "4532" + String.format("%012d", System.currentTimeMillis() % 1000000000000L);
    }

    private String generateCVV() {
        return String.format("%03d", new Random().nextInt(1000));
    }

    private String generatePIN() {
        return String.format("%04d", new Random().nextInt(10000));
    }
}
