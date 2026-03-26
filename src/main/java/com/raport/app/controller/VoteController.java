package com.raport.app.controller;

import com.raport.app.entity.Post;
import com.raport.app.entity.User;
import com.raport.app.entity.Vote;
import com.raport.app.repository.PostRepository;
import com.raport.app.repository.UserRepository;
import com.raport.app.repository.VoteRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin/votes")
public class VoteController {
    private final String rootFolder = "admin/db-tables/";

    private final VoteRepository voteRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public VoteController(VoteRepository voteRepository,
                          UserRepository userRepository,
                          PostRepository postRepository) {
        this.voteRepository = voteRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("votes", voteRepository.findAll());
        return rootFolder + "vote-list";
    }

    @GetMapping("/new")
    public String create(Model model) {
        model.addAttribute("vote", new Vote());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("posts", postRepository.findAll());
        return rootFolder + "vote-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("vote") Vote vote,
                       @RequestParam("userId") Integer userId,
                       @RequestParam("postId") Integer postId,
                       Model model) {

        Optional<User> userOpt = userRepository.findById(userId);
        Optional<Post> postOpt = postRepository.findById(postId);

        if (userOpt.isEmpty() || postOpt.isEmpty()) {
            model.addAttribute("error", "User or Post not found");
            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("posts", postRepository.findAll());
            return rootFolder + "vote-form";
        }

        vote.setUser(userOpt.get());
        vote.setPost(postOpt.get());

        voteRepository.save(vote);
        return "redirect:/admin/votes";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Optional<Vote> optional = voteRepository.findById(id);

        if (optional.isPresent()) {
            model.addAttribute("vote", optional.get());
            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("posts", postRepository.findAll());
            return rootFolder + "vote-form";
        }

        return "redirect:/admin/votes";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Integer id, Model model) {
        Optional<Vote> optional = voteRepository.findById(id);

        if (optional.isPresent()) {
            model.addAttribute("vote", optional.get());
            return rootFolder + "vote-view";
        }

        return "redirect:/admin/votes";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        voteRepository.deleteById(id);
        return "redirect:/admin/votes";
    }
}