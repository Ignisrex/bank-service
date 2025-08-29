package com.bankservice.repository;

import com.bankservice.model.AccountHolder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountHolderRepository extends JpaRepository<AccountHolder, Long> {}
