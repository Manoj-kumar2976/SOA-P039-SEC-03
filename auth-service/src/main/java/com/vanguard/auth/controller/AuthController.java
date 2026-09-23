package com.vanguard.auth.controller;

import com.vanguard.auth.model.AuthResponse;
import com.vanguard.auth.model.LoginRequest;
import com.vanguard.auth.service.UserService;
import com.vanguard.auth.util.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public AuthController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        String role = userService.authenticate(request.getUsername(), request.getPassword());
        if (role == null) {
            return ResponseEntity.status(401).body("{\"error\": \"Invalid credentials\"}");
        }
        String token = jwtUtil.generateToken(request.getUsername(), role);
        return ResponseEntity.ok(new AuthResponse(token, request.getUsername(), role));
    }
}
