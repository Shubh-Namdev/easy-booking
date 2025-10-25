package com.example.booking.controller;

import com.example.booking.dto.UserLoginRequest;
import com.example.booking.dto.UserRegisterRequest;
import com.example.booking.model.Role;
import com.example.booking.model.User;
import com.example.booking.security.JwtUtil;
import com.example.booking.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserController(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserRegisterRequest req) {
        try {
            User u = new User();
            u.setEmail(req.getEmail());
            u.setPassword(req.getPassword());
            u.setFirstName(req.getFirstName());
            u.setLastName(req.getLastName());
            if (req.getRole() != null) {
                try { u.setRole(Role.valueOf(req.getRole())); } catch (Exception e) { u.setRole(Role.CUSTOMER); }
            } else u.setRole(Role.CUSTOMER);
            User saved = userService.register(u);
            String token = jwtUtil.generateToken(saved.getId(), saved.getRole().name());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody UserLoginRequest req) {
        try {
            User u = userService.authenticate(req.getEmail(), req.getPassword());
            String token = jwtUtil.generateToken(u.getId(), u.getRole().name());
            return ResponseEntity.ok(Map.of("token", token));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", "Invalid credentials"));
        }
    }
}
