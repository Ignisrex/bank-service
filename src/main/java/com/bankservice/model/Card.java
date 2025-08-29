package com.bankservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String cardNumber;
    private String cardType; // DEBIT, CREDIT
    private String cvv;
    private String pin;
    private boolean isActive;
    private LocalDate expirationDate;
    
    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;
}
