package com.bankservice.dto;

import lombok.Data;

@Data
public class AccountDTO {
    private String type;
    private boolean primaryFlag;
    private double balance;
}
