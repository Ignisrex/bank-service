package com.bankservice.controller;

import com.bankservice.dto.*;
import com.bankservice.model.AccountHolder;
import com.bankservice.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/")
public class AccountController {
    @Autowired
    private AccountService accountService;

    @PostMapping("signup")
    public AccountHolder signup(@RequestBody SignupRequest request) {
        return accountService.signup(request);
    }

    // Other endpoints would be added here
}
