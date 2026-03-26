package com.raport.app.service.institution;

import org.springframework.stereotype.Service;

@Service
public class InstitutionDashboardService {
    public String getDashboardViewName() {
        return "app.institutii/institutionDash";
    }
}
