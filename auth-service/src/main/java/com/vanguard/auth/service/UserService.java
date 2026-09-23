package com.vanguard.auth.service;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class UserService {

    // Demo in-memory users. Replace with a real user store / DB for production.
    private final Map<String, String[]> users = Map.of(
            "policyholder", new String[]{"pass123", "ROLE_POLICYHOLDER"},
            "adjuster",     new String[]{"pass123", "ROLE_ADJUSTER"},
            "admin",        new String[]{"pass123", "ROLE_ADMIN"}
    );

    public String authenticate(String username, String password) {
        String[] record = users.get(username);
        if (record != null && record[0].equals(password)) {
            return record[1]; // return role
        }
        return null;
    }
}
