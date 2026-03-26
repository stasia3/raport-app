package com.raport.app.controller.auth;

import com.raport.app.entity.PersoanaFizica;
import com.raport.app.entity.User;
import com.raport.app.entity.enums.PersoanaFizicaUserType;
import com.raport.app.entity.enums.UserRole;
import com.raport.app.repository.UserRepository;
import com.raport.app.service.PersoanaFizicaService;
import com.raport.app.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {
    private final String rootFolder = "auth/";

    private final UserService userService;
    private final PersoanaFizicaService persFizService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserService userService, PersoanaFizicaService persFizService, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.persFizService = persFizService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return rootFolder + "login";
    }

//    @PostMapping("/login")
//    public String login(@RequestParam String email,
//                        @RequestParam String password,
//                        Model model) {
//
//        User user = userService.findByEmail(email);
//
//        if (user == null || !user.getPasswordHash().equals(password)) {
//            model.addAttribute("error", "Invalid email or password");
//            return rootFolder + "login";
//        }
//
//        return "redirect:/admin";
//    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("roles", UserRole.values());
        return rootFolder + "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String fname,
                           @RequestParam String lname,
                           @RequestParam String email,
                           @RequestParam String phone,
                           @RequestParam String password,
                           Model model) {

        if (userService.findByEmail(email) != null) {
            model.addAttribute("error", "Email already exists");
            model.addAttribute("roles", UserRole.values());
            return rootFolder + "register";
        }

        // create new account:
        // 1. create user
        // 2. create PersoanaFizica

        User user = new User();
        PersoanaFizica persFiz = new PersoanaFizica();

        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setUserRole(UserRole.Citizen);
        userService.saveUser(user);

        persFiz.setUser(user);
        persFiz.setFirstName(fname);
        persFiz.setLastName(lname);
        persFiz.setPhoneNumber(phone);
        persFiz.setKarmaPoints(0);
        // TODO:check what type should i use
        persFiz.setUserType(PersoanaFizicaUserType.Identificat);
        persFizService.save(persFiz);

        return "redirect:/login";
    }
}

