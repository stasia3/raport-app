package com.raport.app.controller;

import com.raport.app.entity.Institution;
import com.raport.app.entity.User;
import com.raport.app.entity.enums.InstitutionServiceType;
import com.raport.app.repository.UserRepository;
import com.raport.app.service.InstitutionService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/institutions")
public class InstitutionController {
    private final String rootFolder = "admin/db-tables/";

    private final InstitutionService institutionService;
    private final UserRepository userRepository;

    public InstitutionController(InstitutionService institutionService, UserRepository userRepository) {
        this.institutionService = institutionService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("institutions", institutionService.getAll());
        return rootFolder + "institution-list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("institution", new Institution());
        model.addAttribute("serviceTypes", InstitutionServiceType.values());
        model.addAttribute("users", userRepository.findAll());
        return rootFolder + "institution-form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Institution institution,
                         @RequestParam("selectedUserId") Integer selectedUserId) {

        User user = userRepository.findById(selectedUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Institution entity = new Institution();
        entity.setUser(user);
        entity.setOfficialName(institution.getOfficialName());
        entity.setFiscalCode(institution.getFiscalCode());
        entity.setLegalAddress(institution.getLegalAddress());
        entity.setServiceType(institution.getServiceType());

        institutionService.save(entity);

        return "redirect:/institutions";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Institution institution = institutionService.getById(id);

        model.addAttribute("institution", institution);
        model.addAttribute("serviceTypes", InstitutionServiceType.values());

        return rootFolder + "institution-edit-form";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id,
                         @ModelAttribute Institution formData) {

        Institution entity = institutionService.getById(id);

        entity.setOfficialName(formData.getOfficialName());
        entity.setFiscalCode(formData.getFiscalCode());
        entity.setLegalAddress(formData.getLegalAddress());
        entity.setServiceType(formData.getServiceType());

        institutionService.save(entity);

        return "redirect:/institutions";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        institutionService.delete(id);
        return "redirect:/institutions";
    }
}