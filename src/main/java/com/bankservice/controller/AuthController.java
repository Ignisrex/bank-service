package com.bankservice.controller;

import com.bankservice.config.JwtUtil;
import com.bankservice.dto.*;
import com.bankservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest request) {
        try {
            var accountHolder = authService.signup(request);
            String token = jwtUtil.generateToken(accountHolder.getId().toString());
            return ResponseEntity.ok(Map.of(
                "token", token,
                "user", accountHolder,
                "message", "Account created successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        try {
            var accountHolder = authService.authenticate(request.getEmail(), request.getPassword());
            String token = jwtUtil.generateToken(accountHolder.getId().toString());
            return ResponseEntity.ok(Map.of(
                "token", token,
                "user", accountHolder,
                "message", "Login successful"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid credentials"));
        }
    }

    @PostMapping("/card/validate")
    public ResponseEntity<?> validateCard(@Valid @RequestBody CardValidationRequest request) {
        try {
            // Minimal validation logic - in real app this would validate against database
            if ("1234567890123456".equals(request.getCardNumber()) && "123".equals(request.getCvv())) {
                String token = jwtUtil.generateToken(request.getCardNumber());
                return ResponseEntity.ok(Map.of(
                    "token", token,
                    "message", "Card validated successfully"
                ));
            }
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid card details"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
