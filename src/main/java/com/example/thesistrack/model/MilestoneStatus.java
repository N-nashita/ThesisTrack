package com.example.thesistrack.model;

import com.example.thesistrack.exception.BusinessRuleException;

/** State pattern: each status knows its transition rule */
public enum MilestoneStatus {

    PENDING(true) {
        @Override public MilestoneStatus submit() { return SUBMITTED; }
        @Override public MilestoneStatus review(boolean approve) {
            throw new BusinessRuleException("Nothing has been submitted yet");
        }
    },
    SUBMITTED(false) {
        @Override public MilestoneStatus submit() {
            throw new BusinessRuleException("Report already submitted, waiting for review");
        }
        @Override public MilestoneStatus review(boolean approve) {
            return approve ? APPROVED : REVISION_REQUIRED;
        }
    },
    REVISION_REQUIRED(true) {
        @Override public MilestoneStatus submit() { return SUBMITTED; }
        @Override public MilestoneStatus review(boolean approve) {
            return approve ? APPROVED : REVISION_REQUIRED;
        }
    },
    APPROVED(false) {
        @Override public MilestoneStatus submit() {
            throw new BusinessRuleException("This milestone is already approved");
        }
        @Override public MilestoneStatus review(boolean approve) {
            return approve ? APPROVED : REVISION_REQUIRED;   // supervisor can update marks/feedback later
        }
    };

    private final boolean canSubmit;

    MilestoneStatus(boolean canSubmit) { this.canSubmit = canSubmit; }

    public boolean canSubmit() { return canSubmit; }

    /** if student report submit,new status. otherwise exception. */
    public abstract MilestoneStatus submit();

    /** if Supervisor review,new status. */
    public abstract MilestoneStatus review(boolean approve);

    public String label() {
        String[] words = name().replace('_', ' ').toLowerCase().split(" ");
        StringBuilder out = new StringBuilder();
        for (String w : words) out.append(Character.toUpperCase(w.charAt(0))).append(w.substring(1)).append(' ');
        return out.toString().trim();
    }
}