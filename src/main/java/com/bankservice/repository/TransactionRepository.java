package com.bankservice.repository;

import com.bankservice.model.Transaction;
import com.bankservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByAccountOrderByTimestampDesc(Account account);
}
