package com.example.thesistrack.controller;

import com.example.thesistrack.dto.*;
import com.example.thesistrack.model.User;
import com.example.thesistrack.security.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AuthApiController {

    private final CustomUserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/api/auth/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request) {
        CustomUserDetails details;
        try {
            details = (CustomUserDetails) userDetailsService.loadUserByUsername(request.email());
        } catch (UsernameNotFoundException e) {
            throw new BadCredentialsException("Invalid email or password");
        }
        if (!details.isEnabled() || !passwordEncoder.matches(request.password(), details.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }
        User u = details.getUser();
        String token = jwtService.generateToken(u.getEmail(), u.getRole().name());
        return new LoginResponse(token, "Bearer", u.getName(), u.getEmail(), u.getRole());
    }

    @GetMapping("/api/me")
    public UserResponse me(@AuthenticationPrincipal CustomUserDetails me) {
        return UserResponse.from(me.getUser());
    }
}