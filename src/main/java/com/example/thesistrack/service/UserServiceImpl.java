package com.example.thesistrack.service;

import com.example.thesistrack.dto.RegisterRequest;
import com.example.thesistrack.exception.DuplicateEmailException;
import com.example.thesistrack.factory.UserFactory;
import com.example.thesistrack.model.User;
import com.example.thesistrack.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserFactory userFactory;

    @Override
    @Transactional
    public User register(RegisterRequest request) {
        String email = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new DuplicateEmailException(email);
        }
        return userRepository.save(
                userFactory.create(request.name(), email, request.password(), request.role()));
    }
}