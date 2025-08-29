package com.bankservice.model;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type; // CREDIT/DEBIT
    private String number;
    private boolean primaryFlag;
    private double balance;
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    private List<Transaction> transactionList;
    @ManyToMany
    @JoinTable(
        name = "account_holder_account",
        joinColumns = @JoinColumn(name = "account_id"),
        inverseJoinColumns = @JoinColumn(name = "holder_id")
    )
    private List<AccountHolder> holderList;
    @OneToOne(cascade = CascadeType.ALL)
    private Card card;
}
