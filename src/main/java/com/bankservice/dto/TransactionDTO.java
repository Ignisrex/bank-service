package com.bankservice.dto;

import lombok.Data;

@Data
public class TransactionDTO {
    private double amount;
    private String direction;
    private Long accountId;
}
