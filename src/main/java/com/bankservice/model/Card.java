package com.bankservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "account")
@EqualsAndHashCode(exclude = "account")
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String cardNumber;
    private String cardType; // DEBIT, CREDIT
    @JsonIgnore
    private String cvv;
    @JsonIgnore
    private String pin;
    private boolean isActive;
    private LocalDate expirationDate;
    
    @ManyToOne
    @JoinColumn(name = "account_id")
    @JsonIgnore
    private Account account;
}
