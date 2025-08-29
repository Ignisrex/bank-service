package com.bankservice.service;

import com.bankservice.model.*;
import com.bankservice.repository.*;
import com.bankservice.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
                .primaryFlag(dto.isPrimaryFlag())
                .balance(dto.getBalance())
                .holderList(List.of(holder))
                .build();
            accounts.add(acc);
        }
        holder.setAccounts(accounts);
        return accountHolderRepo.save(holder);
    }

    // Other service methods (createAccount, addHolder, getStatements, etc.) would be added here
}
