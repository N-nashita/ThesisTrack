package com.example.thesistrack.service;

import com.example.thesistrack.exception.BusinessRuleException;
import com.example.thesistrack.exception.ResourceNotFoundException;
import com.example.thesistrack.model.*;
import com.example.thesistrack.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubmissionService {

    private static final Set<String> ALLOWED = Set.of("pdf", "doc", "docx");

    private final MilestoneRepository milestoneRepository;
    private final SubmissionRepository submissionRepository;

    @Value("${app.upload-dir:uploads}")
    private String uploadDir;

    @Transactional
    public Submission submit(Long milestoneId, String studentEmail, MultipartFile file) {
        Milestone m = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Milestone not found: " + milestoneId));
        if (!m.getThesis().getStudent().getEmail().equals(studentEmail)) {
            throw new AccessDeniedException("This is not your milestone");
        }

        MilestoneStatus next = m.getStatus().submit();   // State pattern: vul state hole ekhanei exception

        if (file == null || file.isEmpty()) throw new BusinessRuleException("Please choose a file");

        String original = StringUtils.cleanPath(file.getOriginalFilename() == null ? "report" : file.getOriginalFilename());
        String ext = StringUtils.getFilenameExtension(original);
        if (ext == null || !ALLOWED.contains(ext.toLowerCase())) {
            throw new BusinessRuleException("Only PDF, DOC or DOCX files are allowed");
        }

        String stored = UUID.randomUUID() + "." + ext.toLowerCase();
        try {
            Path dir = Path.of(uploadDir).toAbsolutePath();
            Files.createDirectories(dir);
            file.transferTo(dir.resolve(stored));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        Submission s = submissionRepository.save(Submission.builder()
                .milestone(m).documentUrl(stored).originalName(original)
                .version((int) submissionRepository.countByMilestoneId(milestoneId) + 1)
                .submittedAt(LocalDateTime.now()).build());
        m.setStatus(next);
        milestoneRepository.save(m);
        return s;
    }

    public record FileInfo(Path path, String name) {}

    /** Admin, thesis er supervisor, ar thesis er student chara keu file khulte parbe na. */
    public FileInfo fileFor(Long submissionId, String email, boolean isAdmin) {
        Submission s = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found: " + submissionId));
        Thesis t = s.getMilestone().getThesis();
        boolean allowed = isAdmin
                || t.getSupervisor().getEmail().equals(email)
                || t.getStudent().getEmail().equals(email);
        if (!allowed) throw new AccessDeniedException("You cannot open this file");
        return new FileInfo(Path.of(uploadDir).toAbsolutePath().resolve(s.getDocumentUrl()), s.getOriginalName());
    }
}