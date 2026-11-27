package com.example.thesistrack.service;

import com.example.thesistrack.dto.ReviewViews.*;
import com.example.thesistrack.exception.BusinessRuleException;
import com.example.thesistrack.exception.ResourceNotFoundException;
import com.example.thesistrack.model.*;
import com.example.thesistrack.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final SubmissionRepository submissionRepository;
    private final EvaluationRepository evaluationRepository;
    private final MilestoneRepository milestoneRepository;

    public ReviewView reviewView(Long submissionId, String supervisorEmail) {
        Submission s = ownSubmission(submissionId, supervisorEmail);
        List<SubmissionRow> history = submissionRepository
                .findByMilestoneIdOrderByVersionDesc(s.getMilestone().getId()).stream()
                .map(x -> new SubmissionRow(x, evaluationRepository.findBySubmissionId(x.getId()).orElse(null)))
                .toList();
        return new ReviewView(s, evaluationRepository.findBySubmissionId(s.getId()).orElse(null), history);
    }

    /** Return kore thesis id (redirect er jonno). */
    @Transactional
    public Long review(Long submissionId, String supervisorEmail, Remark remark, String comments,
                       Integer marks, boolean approve) {
        Submission s = ownSubmission(submissionId, supervisorEmail);
        Milestone m = s.getMilestone();

        Submission latest = submissionRepository.findFirstByMilestoneIdOrderByVersionDesc(m.getId()).orElseThrow();
        if (!latest.getId().equals(s.getId())) {
            throw new BusinessRuleException("Only the latest submission can be reviewed");
        }

        MilestoneStatus next = m.getStatus().review(approve);   // State pattern

        if (remark == null) throw new BusinessRuleException("Please select a remark");
        if (approve && marks == null) throw new BusinessRuleException("Marks are required to approve");
        if (marks != null && (marks < 0 || marks > m.getTotalMarks())) {
            throw new BusinessRuleException("Marks must be between 0 and " + m.getTotalMarks());
        }
        if (!approve && (comments == null || comments.isBlank())) {
            throw new BusinessRuleException("Please write what needs to be revised");
        }

        Evaluation e = evaluationRepository.findBySubmissionId(submissionId)
                .orElseGet(() -> Evaluation.builder().submission(s).build());
        e.setRemarks(remark);
        e.setComments(comments);
        e.setMarks(marks);
        e.setReviewStatus(next);
        evaluationRepository.save(e);

        m.setStatus(next);
        milestoneRepository.save(m);
        return m.getThesis().getId();
    }

    private Submission ownSubmission(Long id, String supervisorEmail) {
        Submission s = submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found: " + id));
        if (!s.getMilestone().getThesis().getSupervisor().getEmail().equals(supervisorEmail)) {
            throw new AccessDeniedException("This student is not your advisee");
        }
        return s;
    }
}