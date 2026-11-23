package com.example.thesistrack.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "evaluations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    private Submission submission;

    @Enumerated(EnumType.STRING)
    private Remark remarks;

    @Column(length = 2000)
    private String comments;

    private Integer marks;                  // Student will never see this marks.

    @Enumerated(EnumType.STRING)
    private MilestoneStatus reviewStatus;   // APPROVED or REVISION_REQUIRED
}