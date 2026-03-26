package com.raport.app.controller.institution;

import com.raport.app.service.institution.InstitutionDashboardService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/institution")
public class InstitutionDashboardController {

    private final InstitutionDashboardService institutionDashboardService;

    public InstitutionDashboardController(InstitutionDashboardService institutionDashboardService) {
        this.institutionDashboardService = institutionDashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("institutionUserId", 301);
        model.addAttribute("institutionDisplayName", "SERVICIU SALUBRITATE");
        model.addAttribute("institutionServiceType", "Salubritate");
        model.addAttribute("institutionRole", "institution");

        return institutionDashboardService.getDashboardViewName();
    }
}