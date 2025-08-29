package com.bankservice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"transactionList", "holder"})
@EqualsAndHashCode(exclude = {"transactionList", "holder"})
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type; // CREDIT/DEBIT totdo: Make this an enum
    private String number;
    private boolean primaryFlag;
    private double balance;
    @OneToMany(mappedBy = "account", cascade = CascadeType.ALL)
    @JsonIgnore
    private List<Transaction> transactionList;
    @ManyToOne
    @JoinColumn(name = "holder_id")
    @JsonIgnore
    private AccountHolder holder;
    @OneToOne(cascade = CascadeType.ALL)
    private Card card;
}
