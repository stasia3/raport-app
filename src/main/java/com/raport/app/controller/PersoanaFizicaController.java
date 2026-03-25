package com.raport.app.controller;

import com.raport.app.entity.PersoanaFizica;
import com.raport.app.entity.User;
import com.raport.app.entity.enums.PersoanaFizicaUserType;
import com.raport.app.repository.UserRepository;
import com.raport.app.service.PersoanaFizicaService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/persoane-fizice")
public class PersoanaFizicaController {

    private final PersoanaFizicaService persoanaFizicaService;
    private final UserRepository userRepository;

    public PersoanaFizicaController(PersoanaFizicaService persoanaFizicaService, UserRepository userRepository) {
        this.persoanaFizicaService = persoanaFizicaService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("persoane", persoanaFizicaService.getAll());
        return "/admin/db-tables/persoana-fizica-list";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("persoanaFizica", new PersoanaFizica());
        model.addAttribute("userTypes", PersoanaFizicaUserType.values());
        model.addAttribute("users", userRepository.findAll());
        return "persoana-fizica-form";
    }

    @PostMapping("/create")
    public String create(@ModelAttribute PersoanaFizica persoanaFizica,
                         @RequestParam("selectedUserId") Integer selectedUserId) {

        User user = userRepository.findById(selectedUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        PersoanaFizica entity = new PersoanaFizica();
        entity.setUser(user);
        entity.setFirstName(persoanaFizica.getFirstName());
        entity.setLastName(persoanaFizica.getLastName());
        entity.setPhoneNumber(persoanaFizica.getPhoneNumber());
        entity.setKarmaPoints(persoanaFizica.getKarmaPoints());
        entity.setUserType(persoanaFizica.getUserType());

        persoanaFizicaService.save(entity);

        return "redirect:/persoane-fizice";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        PersoanaFizica persoanaFizica = persoanaFizicaService.getById(id);

        model.addAttribute("persoanaFizica", persoanaFizica);
        model.addAttribute("userTypes", PersoanaFizicaUserType.values());

        return "persoana-fizica-edit-form";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Integer id,
                         @ModelAttribute PersoanaFizica formData) {

        PersoanaFizica entity = persoanaFizicaService.getById(id);

        entity.setFirstName(formData.getFirstName());
        entity.setLastName(formData.getLastName());
        entity.setPhoneNumber(formData.getPhoneNumber());
        entity.setKarmaPoints(formData.getKarmaPoints());
        entity.setUserType(formData.getUserType());

        persoanaFizicaService.save(entity);

        return "redirect:/persoane-fizice";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        persoanaFizicaService.delete(id);
        return "redirect:/persoane-fizice";
    }
}