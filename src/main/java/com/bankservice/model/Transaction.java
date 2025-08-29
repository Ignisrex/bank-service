package com.bankservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_transaction")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "account")
@EqualsAndHashCode(exclude = "account")
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
    @JsonIgnore
    private Account account;
}
