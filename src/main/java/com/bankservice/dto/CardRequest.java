package com.bankservice.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
public class CardRequest {
    @NotBlank(message = "Card type is required")
    @Pattern(regexp = "DEBIT|CREDIT", message = "Card type must be DEBIT or CREDIT")
    private String cardType;
    
    private String pin; // Optional, can be generated if not provided
}
