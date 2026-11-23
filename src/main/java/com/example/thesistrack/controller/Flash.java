package com.example.thesistrack.controller;

import com.example.thesistrack.exception.BusinessRuleException;
import com.example.thesistrack.exception.DuplicateEmailException;
import com.example.thesistrack.exception.ResourceNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

final class Flash {
    private Flash() {}

    static String run(RedirectAttributes ra, String okMessage, String redirectTo, Runnable action) {
        try {
            action.run();
            ra.addFlashAttribute("success", okMessage);
        } catch (DuplicateEmailException | BusinessRuleException | ResourceNotFoundException e) {
            ra.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:" + redirectTo;
    }

    static String firstError(BindingResult br) {
        var e = br.getFieldError();
        return e == null ? "Invalid input" : e.getField() + ": " + e.getDefaultMessage();
    }
}