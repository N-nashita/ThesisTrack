package com.example.thesistrack.repository;

import com.example.thesistrack.model.Meeting;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface MeetingRepository extends JpaRepository<Meeting, Long> {
    Optional<Meeting> findByThesisId(Long thesisId);
}