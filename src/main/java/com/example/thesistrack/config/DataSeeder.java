package com.example.thesistrack.config;

import com.example.thesistrack.dto.RegisterRequest;
import com.example.thesistrack.model.Role;
import com.example.thesistrack.repository.UserRepository;
import com.example.thesistrack.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserService userService;
    private final UserRepository userRepository;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail(adminEmail.toLowerCase())) {
            userService.register(new RegisterRequest("System Admin", adminEmail, adminPassword, Role.ADMIN));
        }
    }
}