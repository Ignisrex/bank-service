package com.bankservice.repository;

import com.bankservice.model.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AccountHolderRepository extends JpaRepository<AccountHolder, Long> {
    Optional<AccountHolder> findByEmail(String email);
}
