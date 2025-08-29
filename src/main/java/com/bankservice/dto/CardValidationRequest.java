package com.bankservice.dto;

import lombok.Data;

@Data
public class CardValidationRequest {
    private String cardNumber;
    private String cvv;
}
