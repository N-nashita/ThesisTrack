package com.example.thesistrack.dto;

public final class AdminViews {
    private AdminViews() {}

    public record SupervisorRow(Long userId, String name, String email, String researchInterest,
                                int maxStudents, long assigned) {
        public boolean full() { return assigned >= maxStudents; }
    }
}