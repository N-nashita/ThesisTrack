package com.example.thesistrack.controller;

import com.example.thesistrack.dto.CreateStudentRequest;
import com.example.thesistrack.dto.CreateSupervisorRequest;
import com.example.thesistrack.service.AdminAccountService;
import com.example.thesistrack.service.AssignmentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminController {

    private final AdminAccountService accounts;
    private final AssignmentService assignments;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("studentCount", accounts.getAllStudents().size());
        model.addAttribute("supervisorCount", assignments.supervisorRows().size());
        model.addAttribute("theses", assignments.allTheses());
        return "admin/dashboard";
    }

    @GetMapping("/students")
    public String students(Model model) {
        model.addAttribute("students", accounts.getAllStudents());
        return "admin/students";
    }

    @PostMapping("/students")
    public String createStudent(@Valid CreateStudentRequest req, BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("error", Flash.firstError(br));
            return "redirect:/admin/students";
        }
        return Flash.run(ra, "Student created", "/admin/students", () -> accounts.createStudent(req));
    }

    @GetMapping("/supervisors")
    public String supervisors(Model model) {
        model.addAttribute("supervisors", assignments.supervisorRows());
        return "admin/supervisors";
    }

    @PostMapping("/supervisors")
    public String createSupervisor(@Valid CreateSupervisorRequest req, BindingResult br, RedirectAttributes ra) {
        if (br.hasErrors()) {
            ra.addFlashAttribute("error", Flash.firstError(br));
            return "redirect:/admin/supervisors";
        }
        return Flash.run(ra, "Supervisor created", "/admin/supervisors", () -> accounts.createSupervisor(req));
    }

    @GetMapping("/assign")
    public String assignPage(Model model) {
        model.addAttribute("students", assignments.unassignedStudents());
        model.addAttribute("supervisors", assignments.supervisorRows());
        model.addAttribute("theses", assignments.allTheses());
        return "admin/assign";
    }

    @PostMapping("/assign")
    public String assign(@RequestParam Long studentId, @RequestParam Long supervisorId,
                         @RequestParam(required = false) String title,
                         @RequestParam(required = false) String researchArea, RedirectAttributes ra) {
        return Flash.run(ra, "Supervisor assigned", "/admin/assign",
                () -> assignments.assign(studentId, supervisorId, title, researchArea));
    }
}