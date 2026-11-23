package com.example.thesistrack.repository;

import com.example.thesistrack.model.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MilestoneRepository extends JpaRepository<Milestone, Long> {
    List<Milestone> findByThesisIdOrderByDeadlineAsc(Long thesisId);
}