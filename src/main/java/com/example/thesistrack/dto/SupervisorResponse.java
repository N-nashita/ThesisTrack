package com.example.thesistrack.dto;

import com.example.thesistrack.model.SupervisorProfile;

public record SupervisorResponse(Long userId, String name, String email,
                                 String researchInterest, int maxStudents) {
    public static SupervisorResponse from(SupervisorProfile p) {
        return new SupervisorResponse(p.getUser().getId(), p.getUser().getName(),
                p.getUser().getEmail(), p.getResearchInterest(), p.getMaxStudents());
    }
}