package com.example.thesistrack.dto;

import jakarta.validation.constraints.*;

public record CreateStudentRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6) String password) {}