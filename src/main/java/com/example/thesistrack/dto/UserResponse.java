package com.example.thesistrack.dto;

import com.example.thesistrack.model.Role;
import com.example.thesistrack.model.User;

public record UserResponse(Long id, String name, String email, Role role) {
    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getName(), u.getEmail(), u.getRole());
    }
}