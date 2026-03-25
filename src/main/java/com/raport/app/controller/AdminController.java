package com.raport.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {
    private final String rootFolder = "admin/";

    @GetMapping("/admin")
    public String adminPanel() {
        return rootFolder + "admin-panel";
    }
}