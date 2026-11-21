package com.example.thesistrack.dto;

import com.example.thesistrack.model.Role;

public record LoginResponse(String token, String tokenType, String name, String email, Role role) {}