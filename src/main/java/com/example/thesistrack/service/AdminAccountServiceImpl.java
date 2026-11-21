package com.example.thesistrack.service;

import com.example.thesistrack.dto.*;
import com.example.thesistrack.exception.ResourceNotFoundException;
import com.example.thesistrack.model.*;
import com.example.thesistrack.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)   // open-in-view=false, tai lazy user load er jonno transaction lage
public class AdminAccountServiceImpl implements AdminAccountService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final SupervisorProfileRepository supervisorProfileRepository;

    @Override
    @Transactional
    public UserResponse createStudent(CreateStudentRequest r) {
        User user = userService.register(
                new RegisterRequest(r.name(), r.email(), r.password(), Role.STUDENT));
        return UserResponse.from(user);
    }

    @Override
    @Transactional   // User + SupervisorProfile ek transaction e, ekta fail korle duto-i rollback
    public SupervisorResponse createSupervisor(CreateSupervisorRequest r) {
        User user = userService.register(
                new RegisterRequest(r.name(), r.email(), r.password(), Role.SUPERVISOR));
        SupervisorProfile profile = supervisorProfileRepository.save(
                SupervisorProfile.builder()
                        .user(user)
                        .researchInterest(r.researchInterest().trim())
                        .maxStudents(r.maxStudents())
                        .build());
        return SupervisorResponse.from(profile);
    }

    @Override
    public List<UserResponse> getAllStudents() {
        return userRepository.findByRole(Role.STUDENT).stream().map(UserResponse::from).toList();
    }

    @Override
    public UserResponse getStudent(Long id) {
        return userRepository.findById(id)
                .filter(u -> u.getRole() == Role.STUDENT)
                .map(UserResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + id));
    }

    @Override
    public List<SupervisorResponse> getAllSupervisors() {
        return supervisorProfileRepository.findAll().stream().map(SupervisorResponse::from).toList();
    }
}