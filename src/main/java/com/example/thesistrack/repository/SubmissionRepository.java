package com.example.thesistrack.repository;

import com.example.thesistrack.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface SubmissionRepository extends JpaRepository<Submission, Long> {
    List<Submission> findByMilestoneIdOrderByVersionDesc(Long milestoneId);
    Optional<Submission> findFirstByMilestoneIdOrderByVersionDesc(Long milestoneId);
    long countByMilestoneId(Long milestoneId);
}