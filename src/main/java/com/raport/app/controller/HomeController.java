package com.raport.app.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("title", "Prima mea pagină");
        model.addAttribute("message", "Aplicația merge cu Spring Boot + Thymeleaf.");
        return "home";
    }
}