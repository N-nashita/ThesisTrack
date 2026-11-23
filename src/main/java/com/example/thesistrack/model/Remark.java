package com.example.thesistrack.model;

public enum Remark {
    EXCELLENT, GOOD, MEDIUM, POOR, BAD;

    public String label() { return name().charAt(0) + name().substring(1).toLowerCase(); }
}