package com.example.thesistrack.service;

import com.example.thesistrack.dto.RegisterRequest;
import com.example.thesistrack.model.User;

public interface UserService {
    User register(RegisterRequest request);
}