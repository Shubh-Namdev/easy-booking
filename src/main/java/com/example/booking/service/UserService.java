package com.example.booking.service;

import com.example.booking.model.Role;
import com.example.booking.model.User;
import com.example.booking.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@Service
public class UserService {
    private final UserRepository repo;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public User register(User user) {
        if (repo.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException("User already exists");
        }
        user.setPassword(encoder.encode(user.getPassword()));
        if (user.getRole() == null) user.setRole(Role.CUSTOMER);
        return repo.save(user);
    }

    public User authenticate(String email, String rawPassword) {
        var u = repo.findByEmail(email).orElseThrow(() -> new NoSuchElementException("Invalid credentials"));
        if (!encoder.matches(rawPassword, u.getPassword())) {
            throw new NoSuchElementException("Invalid credentials");
        }
        return u;
    }
}
