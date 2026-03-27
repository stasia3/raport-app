package com.raport.app.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    private final String rootFolder = "admin/";

    @GetMapping("/admin")
    public String adminPanel(Model model, Authentication authentication) {
        boolean isAuthenticated = authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName());

        model.addAttribute("isAuthenticated", isAuthenticated);

        return "admin/admin-panel";
    }
}