package com.example.thesistrack.controller;

import com.example.thesistrack.exception.ResourceNotFoundException;
import com.example.thesistrack.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
public class DownloadController {

    private final SubmissionService submissionService;

    @GetMapping("/submissions/{id}/download")
    public ResponseEntity<Resource> download(@PathVariable Long id, Authentication auth) {
        boolean admin = auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        var file = submissionService.fileFor(id, auth.getName(), admin);
        Resource resource = new FileSystemResource(file.path());
        if (!resource.exists()) throw new ResourceNotFoundException("File not found on server");
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        ContentDisposition.attachment().filename(file.name()).build().toString())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }
}