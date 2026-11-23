package com.example.thesistrack.repository;

import com.example.thesistrack.model.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    Optional<Evaluation> findBySubmissionId(Long submissionId);
    List<Evaluation> findBySubmissionMilestoneId(Long milestoneId);
}