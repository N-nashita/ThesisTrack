package com.example.thesistrack.security;

import jakarta.servlet.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;
import java.util.Map;

@Component
public class RoleBasedSuccessHandler implements AuthenticationSuccessHandler {

    private static final Map<String, String> ROLE_TO_URL = Map.of(
            "ROLE_ADMIN", "/admin/dashboard",
            "ROLE_SUPERVISOR", "/supervisor/dashboard",
            "ROLE_STUDENT", "/student/dashboard");

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        String target = authentication.getAuthorities().stream()
                .map(a -> ROLE_TO_URL.get(a.getAuthority()))
                .filter(java.util.Objects::nonNull)
                .findFirst().orElse("/login?error");
        response.sendRedirect(request.getContextPath() + target);
    }
}