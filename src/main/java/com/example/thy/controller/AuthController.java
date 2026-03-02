package com.example.thy.controller;

import com.example.thy.dto.LoginRequest;
import com.example.thy.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private JwtUtils jwtUtils;

    // Not: Normalde burada DB'den User kontrolü yapılır.
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        if ("admin".equals(loginRequest.getUsername()) && "admin123".equals(loginRequest.getPassword())) {
            String token = jwtUtils.generateToken("admin", "ADMIN");
            return ResponseEntity.ok(Map.of("token", token, "role", "ADMIN"));
        } else if ("agency".equals(loginRequest.getUsername()) && "agency123".equals(loginRequest.getPassword())) {
            String token = jwtUtils.generateToken("agency", "AGENCY");
            return ResponseEntity.ok(Map.of("token", token, "role", "AGENCY"));
        }
        return ResponseEntity.status(401).body("Hatalı kullanıcı adı veya şifre");
    }
}