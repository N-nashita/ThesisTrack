package com.example.thesistrack.dto;

import com.example.thesistrack.model.*;
import java.util.List;

public final class SupervisorViews {
    private SupervisorViews() {}

    public record AdviseeRow(Thesis thesis, int total, int approved) {
        public int percent() { return total == 0 ? 0 : approved * 100 / total; }
    }
    public record MilestoneRow(Milestone milestone, Submission latest, Evaluation evaluation) {}
    public record ThesisView(Thesis thesis, Meeting meeting, List<MilestoneRow> rows) {}
}