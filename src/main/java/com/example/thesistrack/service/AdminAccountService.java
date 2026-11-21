package com.example.thesistrack.service;

import com.example.thesistrack.dto.*;
import java.util.List;

public interface AdminAccountService {
    UserResponse createStudent(CreateStudentRequest request);
    SupervisorResponse createSupervisor(CreateSupervisorRequest request);
    List<UserResponse> getAllStudents();
    UserResponse getStudent(Long id);
    List<SupervisorResponse> getAllSupervisors();
}