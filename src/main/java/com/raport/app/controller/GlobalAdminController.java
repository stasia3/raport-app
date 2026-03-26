package com.raport.app.controller;

import com.raport.app.entity.GlobalAdmin;
import com.raport.app.entity.User;
import com.raport.app.entity.enums.GlobalAdminClearance;
import com.raport.app.repository.UserRepository;
import com.raport.app.service.GlobalAdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/global-admins")
public class GlobalAdminController {

    private final GlobalAdminService globalAdminService;
    private final UserRepository userRepository;

    public GlobalAdminController(GlobalAdminService globalAdminService, UserRepository userRepository) {
        this.globalAdminService = globalAdminService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("globalAdmins", globalAdminService.getAll());
        return "admin/db-tables/global-admin-list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("globalAdmin", new GlobalAdmin());
        model.addAttribute("clearances", GlobalAdminClearance.values());
        model.addAttribute("users", userRepository.findAll());
        return "admin/db-tables/global-admin-form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute GlobalAdmin globalAdmin,
                         @RequestParam("selectedUserId") Integer selectedUserId) {

        User user = userRepository.findById(selectedUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        GlobalAdmin entity = new GlobalAdmin();
        entity.setUser(user);
        entity.setAdminHandle(globalAdmin.getAdminHandle());
        entity.setSecurityClearance(globalAdmin.getSecurityClearance());
        entity.setMasterKeyHash(globalAdmin.getMasterKeyHash());
        entity.setCanManageTags(globalAdmin.getCanManageTags() != null ? globalAdmin.getCanManageTags() : false);
        entity.setCanManageUsers(globalAdmin.getCanManageUsers() != null ? globalAdmin.getCanManageUsers() : false);
        entity.setLastSecurityAudit(globalAdmin.getLastSecurityAudit());

        globalAdminService.save(entity);

        return "redirect:/global-admins";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        GlobalAdmin globalAdmin = globalAdminService.getById(id);

        model.addAttribute("globalAdmin", globalAdmin);
        model.addAttribute("clearances", GlobalAdminClearance.values());

        return "admin/db-tables/global-admin-edit-form";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id,
                         @ModelAttribute GlobalAdmin formData) {

        GlobalAdmin entity = globalAdminService.getById(id);

        entity.setAdminHandle(formData.getAdminHandle());
        entity.setSecurityClearance(formData.getSecurityClearance());
        entity.setMasterKeyHash(formData.getMasterKeyHash());
        entity.setCanManageTags(formData.getCanManageTags() != null ? formData.getCanManageTags() : false);
        entity.setCanManageUsers(formData.getCanManageUsers() != null ? formData.getCanManageUsers() : false);
        entity.setLastSecurityAudit(formData.getLastSecurityAudit());

        globalAdminService.save(entity);

        return "redirect:/global-admins";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        globalAdminService.delete(id);
        return "redirect:/global-admins";
    }
}
