package com.bankservice.repository;

import com.bankservice.model.Card;
import com.bankservice.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByAccount(Account account);
}
