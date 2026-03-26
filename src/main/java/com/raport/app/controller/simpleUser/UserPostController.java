package com.raport.app.controller.simpleUser;

import com.raport.app.entity.PersoanaFizica;
import com.raport.app.entity.Post;
import com.raport.app.entity.User;
import com.raport.app.repository.PersoanaFizicaRepository;
import com.raport.app.repository.PostRepository;
import com.raport.app.repository.UserRepository;
import com.raport.app.service.PostService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Optional;
@Controller
@RequestMapping("/posts")
public class UserPostController {
    private final String rootFolder = "app/simpleUser/";

    private final PostService postService;
    private final PersoanaFizicaRepository persoanaFizicaRepository;
    private final UserRepository userRepository;

    public UserPostController(PostService postService,
                              PersoanaFizicaRepository persoanaFizicaRepository, UserRepository userRepository) {
        this.postService = postService;
        this.persoanaFizicaRepository = persoanaFizicaRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model, Authentication authentication) {
        Post post = new Post();
        post.setIsAnonymous(false);

        boolean isAuthenticated = authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName());

        model.addAttribute("post", post);
        model.addAttribute("isAuthenticated", isAuthenticated);

        return rootFolder + "createPost";
    }

    @PostMapping("/save")
    public String savePost(@ModelAttribute("post") Post post,
                           @RequestParam(value = "images", required = false) MultipartFile[] images,
                           Authentication authentication,
                           Model model) {

        boolean isAuthenticated = authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName());

        PersoanaFizica creator = null;

        if (isAuthenticated) {
            String email = authentication.getName();
            User user = userRepository.findByEmail(email).orElse(null);

            if (user != null) {
                creator = persoanaFizicaRepository.findByUser(user).orElse(null);
            }
        }

        try {
            postService.createPost(post, creator, isAuthenticated, images);
            return "redirect:/feed";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("post", post);
            model.addAttribute("isAuthenticated", isAuthenticated);
            model.addAttribute("error", ex.getMessage());
            return rootFolder + "createPost";
        }
    }
}
