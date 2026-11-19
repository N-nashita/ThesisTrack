package com.example.thesistrack.controller;

import com.example.thesistrack.security.CustomUserDetails;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    @GetMapping("/admin/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public String admin(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        return render("Admin Dashboard", me, model);
    }

    @GetMapping("/supervisor/dashboard")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public String supervisor(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        return render("Supervisor Dashboard", me, model);
    }

    @GetMapping("/student/dashboard")
    @PreAuthorize("hasRole('STUDENT')")
    public String student(@AuthenticationPrincipal CustomUserDetails me, Model model) {
        return render("Student Dashboard", me, model);
    }

    private String render(String title, CustomUserDetails me, Model model) {
        model.addAttribute("title", title);
        model.addAttribute("name", me.getUser().getName());
        return "dashboard";
    }
}