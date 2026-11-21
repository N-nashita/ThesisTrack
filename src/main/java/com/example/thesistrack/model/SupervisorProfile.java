package com.example.thesistrack.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "supervisor_profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SupervisorProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String researchInterest;

    @Column(nullable = false)
    private int maxStudents;
}