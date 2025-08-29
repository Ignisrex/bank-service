package com.bankservice.dto;

import lombok.Data;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

@Data
public class TransferRequest {
    @NotNull(message = "From account is required")
    @Positive(message = "Account ID must be positive")
    private Long fromAccountId;
    
    @NotNull(message = "To account is required")
    @Positive(message = "Account ID must be positive")
    private Long toAccountId;
    
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be at least 0.01")
    private BigDecimal amount;
    
    @Size(max = 255, message = "Description too long")
    private String description;
}
