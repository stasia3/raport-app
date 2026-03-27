package com.raport.app.controller.app;

import com.raport.app.entity.Post;
import com.raport.app.entity.User;
import com.raport.app.repository.PostRepository;
import com.raport.app.repository.UserRepository;
import com.raport.app.service.FeedService;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
public class FeedController {
    private final String rootFolder = "app/simpleUser/";

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FeedService feedService;

    public FeedController(PostRepository postRepository, UserRepository userRepository, FeedService feedService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.feedService = feedService;
    }

//    @GetMapping("/feed")
//    public String showFeedPage(Model model) {
//        model.addAttribute("posts", postRepository.findAll());
//        return  rootFolder + "feed";
//    }

    @GetMapping("/feed")
    public String showFeed(@RequestParam(required = false) String status, Authentication authentication, Model model) {

//        List<Post> posts = new ArrayList<>();
//        if(status != null && !status.isEmpty()){
//            posts = feedService.getPostsForFeed(status);
//        } else {
//            posts = postRepository.findAll();
//        }
        List<Post> posts = feedService.getPostsForFeed(status);
        model.addAttribute("posts", posts);
        model.addAttribute("postsCount", posts.size());
        model.addAttribute("selectedStatus", status);

        boolean loggedIn = authentication != null
                && authentication.isAuthenticated()
                && !(authentication instanceof AnonymousAuthenticationToken);

        model.addAttribute("isLoggedIn", loggedIn);

        if (loggedIn) {
            String username = authentication.getName();

            Optional<User> userOptional = userRepository.findByEmail(username);
            if (userOptional.isPresent()) {
                User currentUser = userOptional.get();
                model.addAttribute("currentUser", currentUser);
                model.addAttribute("currentUsername", username);
            }
        }

        return rootFolder + "feed";
    }

}