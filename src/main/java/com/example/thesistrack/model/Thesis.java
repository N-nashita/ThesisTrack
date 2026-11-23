package com.example.thesistrack.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "theses")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Thesis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "student_id", nullable = false, unique = true)
    private User student;

    @ManyToOne(optional = false)
    @JoinColumn(name = "supervisor_id", nullable = false)
    private User supervisor;

    private String title;
    private String researchArea;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private ThesisStatus status = ThesisStatus.ONGOING;
}