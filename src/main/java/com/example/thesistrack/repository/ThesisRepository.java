package com.example.thesistrack.repository;

import com.example.thesistrack.model.Thesis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ThesisRepository extends JpaRepository<Thesis, Long> {
    boolean existsByStudentId(Long studentId);
    long countBySupervisorId(Long supervisorId);
    Optional<Thesis> findByStudentEmail(String email);
    List<Thesis> findBySupervisorEmail(String email);
}