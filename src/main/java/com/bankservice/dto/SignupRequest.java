package com.bankservice.dto;

import lombok.Data;
import java.util.List;

@Data
public class SignupRequest {
    private String name;
    private List<AccountDTO> accounts;
}
