package com.raport.app.controller;

import com.raport.app.entity.PersoanaFizica;
import com.raport.app.entity.Post;
import com.raport.app.entity.User;
import com.raport.app.repository.PersoanaFizicaRepository;
import com.raport.app.repository.PostRepository;
import com.raport.app.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/admin/posts")
public class PostController {
    private final String rootFolder = "admin/db-tables/";

    private final PostRepository postRepository;
    private final PersoanaFizicaRepository persoanaFizicaRepository;

    public PostController(PostRepository postRepository,
                          PersoanaFizicaRepository persoanaFizicaRepository) {
        this.postRepository = postRepository;
        this.persoanaFizicaRepository = persoanaFizicaRepository;
    }

    @GetMapping
    public String listPosts(Model model) {
        model.addAttribute("posts", postRepository.findAll());
        return rootFolder + "post-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Post post = new Post();
        post.setIsAnonymous(false);

        model.addAttribute("post", post);
        model.addAttribute("creators", persoanaFizicaRepository.findAll());
        return rootFolder + "post-form";
    }

    @PostMapping("/save")
    public String savePost(@ModelAttribute("post") Post post,
                           @RequestParam("creatorId") Integer creatorId,
                           Model model) {

        Optional<PersoanaFizica> creatorOptional = persoanaFizicaRepository.findById(creatorId);

        if (creatorOptional.isEmpty()) {
            model.addAttribute("error", "Creator not found");
            model.addAttribute("creators", persoanaFizicaRepository.findAll());
            return rootFolder + "post-form";
        }

        post.setCreator(creatorOptional.get());

        if (post.getId() == null) {
            post.setCreatedAt(LocalDateTime.now());
        } else {
            Post existingPost = postRepository.findById(post.getId()).orElse(null);
            if (existingPost != null) {
                post.setCreatedAt(existingPost.getCreatedAt());
            } else if (post.getCreatedAt() == null) {
                post.setCreatedAt(LocalDateTime.now());
            }
        }

        if (post.getIsAnonymous() == null) {
            post.setIsAnonymous(false);
        }

        postRepository.save(post);
        return "redirect:/admin/posts";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Optional<Post> postOptional = postRepository.findById(id);

        if (postOptional.isPresent()) {
            model.addAttribute("post", postOptional.get());
            model.addAttribute("creators", persoanaFizicaRepository.findAll());
            return rootFolder + "post-form";
        }

        return "redirect:/admin/posts";
    }

    @GetMapping("/view/{id}")
    public String viewPost(@PathVariable Integer id, Model model) {
        Optional<Post> postOptional = postRepository.findById(id);

        if (postOptional.isPresent()) {
            model.addAttribute("post", postOptional.get());
            return rootFolder + "post-view";
        }

        return "redirect:/admin/posts";
    }

    @GetMapping("/delete/{id}")
    public String deletePost(@PathVariable Integer id) {
        postRepository.deleteById(id);
        return "redirect:/admin/posts";
    }
}