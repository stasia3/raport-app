package com.raport.app.controller;

import com.raport.app.entity.SystemLogs;
import com.raport.app.entity.User;
import com.raport.app.repository.SystemLogsRepository;
import com.raport.app.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/admin/system-logs")
public class SystemLogsController {
    private final String rootFolder = "admin/db-tables/";

    private final SystemLogsRepository systemLogsRepository;
    private final UserRepository userRepository;

    public SystemLogsController(SystemLogsRepository systemLogsRepository,
                                UserRepository userRepository) {
        this.systemLogsRepository = systemLogsRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String listLogs(Model model) {
        model.addAttribute("logs", systemLogsRepository.findAll());
        return rootFolder + "system-logs-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("systemLog", new SystemLogs());
        model.addAttribute("users", userRepository.findAll());
        return rootFolder + "system-logs-form";
    }

    @PostMapping("/save")
    public String saveLog(@ModelAttribute("systemLog") SystemLogs systemLog,
                          @RequestParam(value = "actorUserId", required = false) Integer actorUserId,
                          Model model) {

        if (actorUserId != null) {
            Optional<User> userOptional = userRepository.findById(actorUserId);
            systemLog.setActorUser(userOptional.orElse(null));
        } else {
            systemLog.setActorUser(null);
        }

        if (systemLog.getId() == null) {
            systemLog.setTimestamp(LocalDateTime.now());
        } else {
            SystemLogs existingLog = systemLogsRepository.findById(systemLog.getId()).orElse(null);
            if (existingLog != null) {
                systemLog.setTimestamp(existingLog.getTimestamp());
            } else if (systemLog.getTimestamp() == null) {
                systemLog.setTimestamp(LocalDateTime.now());
            }
        }

        systemLogsRepository.save(systemLog);
        return "redirect:/admin/system-logs";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Optional<SystemLogs> logOptional = systemLogsRepository.findById(id);

        if (logOptional.isPresent()) {
            model.addAttribute("systemLog", logOptional.get());
            model.addAttribute("users", userRepository.findAll());
            return rootFolder + "system-logs-form";
        }

        return "redirect:/admin/system-logs";
    }

    @GetMapping("/view/{id}")
    public String viewLog(@PathVariable Integer id, Model model) {
        Optional<SystemLogs> logOptional = systemLogsRepository.findById(id);

        if (logOptional.isPresent()) {
            model.addAttribute("systemLog", logOptional.get());
            return rootFolder + "system-logs-view";
        }

        return "redirect:/admin/system-logs";
    }

    @GetMapping("/delete/{id}")
    public String deleteLog(@PathVariable Integer id) {
        systemLogsRepository.deleteById(id);
        return "redirect:/admin/system-logs";
    }
}