package com.example.thesistrack.service;

import com.example.thesistrack.dto.SupervisorViews.*;
import com.example.thesistrack.exception.BusinessRuleException;
import com.example.thesistrack.exception.ResourceNotFoundException;
import com.example.thesistrack.model.*;
import com.example.thesistrack.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SupervisorService {

    private final ThesisRepository thesisRepository;
    private final MeetingRepository meetingRepository;
    private final MilestoneRepository milestoneRepository;
    private final SubmissionRepository submissionRepository;
    private final EvaluationRepository evaluationRepository;

    public List<AdviseeRow> advisees(String supervisorEmail) {
        return thesisRepository.findBySupervisorEmail(supervisorEmail).stream().map(t -> {
            List<Milestone> ms = milestoneRepository.findByThesisIdOrderByDeadlineAsc(t.getId());
            int approved = (int) ms.stream().filter(m -> m.getStatus() == MilestoneStatus.APPROVED).count();
            return new AdviseeRow(t, ms.size(), approved);
        }).toList();
    }

    public ThesisView thesisView(Long thesisId, String supervisorEmail) {
        Thesis thesis = ownThesis(thesisId, supervisorEmail);
        Meeting meeting = meetingRepository.findByThesisId(thesisId).orElse(null);
        List<MilestoneRow> rows = milestoneRepository.findByThesisIdOrderByDeadlineAsc(thesisId).stream().map(m -> {
            Submission latest = submissionRepository.findFirstByMilestoneIdOrderByVersionDesc(m.getId()).orElse(null);
            Evaluation ev = latest == null ? null : evaluationRepository.findBySubmissionId(latest.getId()).orElse(null);
            return new MilestoneRow(m, latest, ev);
        }).toList();
        return new ThesisView(thesis, meeting, rows);
    }

    @Transactional
    public void saveMeeting(Long thesisId, String supervisorEmail, LocalDate date, LocalTime time) {
        Thesis thesis = ownThesis(thesisId, supervisorEmail);
        if (date == null || time == null) throw new BusinessRuleException("Meeting date and time are required");
        Meeting meeting = meetingRepository.findByThesisId(thesisId)
                .orElseGet(() -> Meeting.builder().thesis(thesis).build());
        meeting.setMeetingDate(date);
        meeting.setMeetingTime(time);
        meetingRepository.save(meeting);
    }

    @Transactional
    public void addMilestone(Long thesisId, String supervisorEmail, String title, String description,
                             LocalDate deadline, int totalMarks) {
        Thesis thesis = ownThesis(thesisId, supervisorEmail);
        check(title, deadline, totalMarks);
        milestoneRepository.save(Milestone.builder()
                .thesis(thesis).title(title.trim()).description(description)
                .deadline(deadline).totalMarks(totalMarks).build());
    }

    @Transactional
    public void updateMilestone(Long milestoneId, String supervisorEmail, String title, String description,
                                LocalDate deadline, int totalMarks) {
        Milestone m = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found: " + milestoneId));
        ownThesis(m.getThesis().getId(), supervisorEmail);
        check(title, deadline, totalMarks);
        boolean lowerThanGiven = evaluationRepository.findBySubmissionMilestoneId(milestoneId).stream()
                .anyMatch(e -> e.getMarks() != null && e.getMarks() > totalMarks);
        if (lowerThanGiven) throw new BusinessRuleException("Maximum marks cannot be lower than marks already given");
        m.setTitle(title.trim());
        m.setDescription(description);
        m.setDeadline(deadline);
        m.setTotalMarks(totalMarks);
        milestoneRepository.save(m);
    }

    /** if the thesis is not of this supervisor it gives 403 */
    public Thesis ownThesis(Long thesisId, String supervisorEmail) {
        Thesis thesis = thesisRepository.findById(thesisId)
                .orElseThrow(() -> new ResourceNotFoundException("Thesis not found: " + thesisId));
        if (!thesis.getSupervisor().getEmail().equals(supervisorEmail)) {
            throw new AccessDeniedException("This student is not your advisee");
        }
        return thesis;
    }

    private void check(String title, LocalDate deadline, int totalMarks) {
        if (title == null || title.isBlank()) throw new BusinessRuleException("Milestone title is required");
        if (deadline == null) throw new BusinessRuleException("Deadline is required");
        if (totalMarks < 1) throw new BusinessRuleException("Maximum marks must be at least 1");
    }
}