package com.example.thesistrack.dto;

import com.example.thesistrack.model.*;
import java.util.List;

public final class ReviewViews {
    private ReviewViews() {}

    public record SubmissionRow(Submission submission, Evaluation evaluation) {}
    public record ReviewView(Submission submission, Evaluation evaluation, List<SubmissionRow> history) {}
}