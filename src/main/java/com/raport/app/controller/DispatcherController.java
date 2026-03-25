package com.raport.app.controller;

import com.raport.app.entity.Dispatcher;
import com.raport.app.entity.User;
import com.raport.app.entity.enums.DispatcherRole;
import com.raport.app.repository.UserRepository;
import com.raport.app.service.DispatcherService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dispatchers")
public class DispatcherController {
    private final String rootFolder = "admin/db-tables/";

    private final DispatcherService dispatcherService;
    private final UserRepository userRepository;

    public DispatcherController(DispatcherService dispatcherService, UserRepository userRepository) {
        this.dispatcherService = dispatcherService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("dispatchers", dispatcherService.getAll());
        return rootFolder + "dispatcher-list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("dispatcher", new Dispatcher());
        model.addAttribute("roles", DispatcherRole.values());
        model.addAttribute("users", userRepository.findAll());
        return rootFolder + "dispatcher-form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute Dispatcher dispatcher,
                         @RequestParam("selectedUserId") Integer selectedUserId) {

        User user = userRepository.findById(selectedUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Dispatcher entity = new Dispatcher();
        entity.setUser(user);
        entity.setFullName(dispatcher.getFullName());
        entity.setRole(dispatcher.getRole());
        entity.setAccessLevel(dispatcher.getAccessLevel());

        dispatcherService.save(entity);

        return "redirect:/dispatchers";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Dispatcher dispatcher = dispatcherService.getById(id);

        model.addAttribute("dispatcher", dispatcher);
        model.addAttribute("roles", DispatcherRole.values());

        return rootFolder + "dispatcher-edit-form";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id,
                         @ModelAttribute Dispatcher formData) {

        Dispatcher entity = dispatcherService.getById(id);

        entity.setFullName(formData.getFullName());
        entity.setRole(formData.getRole());
        entity.setAccessLevel(formData.getAccessLevel());

        dispatcherService.save(entity);

        return "redirect:/dispatchers";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        dispatcherService.delete(id);
        return "redirect:/dispatchers";
    }
}