package com.example.thesistrack.service;

import com.example.thesistrack.dto.AdminViews.SupervisorRow;
import com.example.thesistrack.exception.BusinessRuleException;
import com.example.thesistrack.exception.ResourceNotFoundException;
import com.example.thesistrack.model.*;
import com.example.thesistrack.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AssignmentService {

    public static final int MAX_STUDENTS = 3;

    private final UserRepository userRepository;
    private final ThesisRepository thesisRepository;
    private final SupervisorProfileRepository supervisorProfileRepository;

    @Transactional
    public Thesis assign(Long studentId, Long supervisorId, String title, String researchArea) {
        User student = userRepository.findById(studentId)
                .filter(u -> u.getRole() == Role.STUDENT)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found: " + studentId));
        User supervisor = userRepository.findById(supervisorId)
                .filter(u -> u.getRole() == Role.SUPERVISOR)
                .orElseThrow(() -> new ResourceNotFoundException("Supervisor not found: " + supervisorId));
        SupervisorProfile profile = supervisorProfileRepository.findByUserId(supervisorId)
                .orElseThrow(() -> new ResourceNotFoundException("Supervisor profile not found"));

        if (thesisRepository.existsByStudentId(studentId)) {
            throw new BusinessRuleException("This student already has a supervisor");
        }
        int capacity = Math.min(profile.getMaxStudents(), MAX_STUDENTS);
        if (thesisRepository.countBySupervisorId(supervisorId) >= capacity) {
            throw new BusinessRuleException("Supervisor capacity is full (" + capacity + " students)");
        }
        return thesisRepository.save(Thesis.builder()
                .student(student)
                .supervisor(supervisor)
                .title(title == null || title.isBlank() ? "Untitled thesis" : title.trim())
                .researchArea(researchArea == null ? "" : researchArea.trim())
                .build());
    }

    public List<Thesis> allTheses() { return thesisRepository.findAll(); }

    public List<User> unassignedStudents() {
        return userRepository.findByRole(Role.STUDENT).stream()
                .filter(s -> !thesisRepository.existsByStudentId(s.getId()))
                .toList();
    }

    public List<SupervisorRow> supervisorRows() {
        return supervisorProfileRepository.findAll().stream()
                .map(p -> new SupervisorRow(p.getUser().getId(), p.getUser().getName(), p.getUser().getEmail(),
                        p.getResearchInterest(), Math.min(p.getMaxStudents(), MAX_STUDENTS),
                        thesisRepository.countBySupervisorId(p.getUser().getId())))
                .toList();
    }
}