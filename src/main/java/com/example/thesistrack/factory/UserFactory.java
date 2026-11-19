package com.example.thesistrack.factory;

import com.example.thesistrack.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserFactory {

    private final PasswordEncoder passwordEncoder;

    public User create(String name, String email, String rawPassword, Role role) {
        return User.builder()
                .name(name.trim())
                .email(email.trim().toLowerCase())
                .password(passwordEncoder.encode(rawPassword))
                .role(role)
                .build();
    }
}