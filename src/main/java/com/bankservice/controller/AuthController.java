package com.bankservice.controller;

import com.bankservice.config.JwtUtil;
import com.bankservice.dto.CardValidationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/card/validate")
    public String validateCard(@RequestBody CardValidationRequest request) {
        // Minimal validation logic (stub)
        if ("1234567890123456".equals(request.getCardNumber()) && "123".equals(request.getCvv())) {
            return jwtUtil.generateToken(request.getCardNumber());
        }
        throw new RuntimeException("Invalid card");
    }
}
