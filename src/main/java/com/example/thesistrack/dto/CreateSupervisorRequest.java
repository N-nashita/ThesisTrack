package com.example.thesistrack.dto;

import jakarta.validation.constraints.*;

public record CreateSupervisorRequest(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank @Size(min = 6) String password,
        @NotBlank String researchInterest,
        @Min(1) @Max(3) int maxStudents) {}