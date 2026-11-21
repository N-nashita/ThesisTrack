package com.example.thesistrack.controller;

import com.example.thesistrack.dto.*;
import com.example.thesistrack.service.AdminAccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminAccountController {

    private final AdminAccountService accountService;

    @PostMapping("/students")
    @ResponseStatus(HttpStatus.CREATED)
    public UserResponse createStudent(@Valid @RequestBody CreateStudentRequest request) {
        return accountService.createStudent(request);
    }

    @GetMapping("/students")
    public List<UserResponse> students() { return accountService.getAllStudents(); }

    @GetMapping("/students/{id}")
    public UserResponse student(@PathVariable Long id) { return accountService.getStudent(id); }

    @PostMapping("/supervisors")
    @ResponseStatus(HttpStatus.CREATED)
    public SupervisorResponse createSupervisor(@Valid @RequestBody CreateSupervisorRequest request) {
        return accountService.createSupervisor(request);
    }

    @GetMapping("/supervisors")
    public List<SupervisorResponse> supervisors() { return accountService.getAllSupervisors(); }
}