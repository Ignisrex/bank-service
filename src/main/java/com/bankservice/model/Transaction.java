package com.bankservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime timestamp;
    private double amount;
    private String direction; // CREDIT, DEBIT, OUT, IN
    private String description;
    
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
