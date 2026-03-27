package com.raport.app.controller.dispatcher;

import com.raport.app.entity.enums.TicketStatus;
import com.raport.app.service.DispatcherService;
import com.raport.app.service.DispatcherUserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/dispatcher")
public class DispatcherUserController {
    private final String rootFolder = "app/dispatcher/";

    private final DispatcherUserService dispatcherUserService;

    public DispatcherUserController(DispatcherUserService dispatcherUserService) {
        this.dispatcherUserService = dispatcherUserService;
    }

    @GetMapping("/posts")
    public String showPosts(Model model, Authentication authentication) {
        boolean isAuthenticated = authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getName());

        model.addAttribute("posts", dispatcherUserService.getAllPostsForDispatcher());
        model.addAttribute("ticketStatuses", TicketStatus.values());
        model.addAttribute("institutionUsers", dispatcherUserService.getInstitutionUsers());
        model.addAttribute("isAuthenticated", isAuthenticated);

        return rootFolder + "post-list";
    }

    @PostMapping("/posts/{postId}/take")
    public String takePost(@PathVariable Integer postId,
                           Authentication authentication,
                           Model model) {
        try {
            dispatcherUserService.takePost(postId, authentication.getName());
            return "redirect:/dispatcher/posts";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("posts", dispatcherUserService.getAllPostsForDispatcher());
            model.addAttribute("ticketStatuses", TicketStatus.values());
            model.addAttribute("institutionUsers", dispatcherUserService.getInstitutionUsers());
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("isAuthenticated", true);
            return rootFolder + "post-list";
        }
    }

    @PostMapping("/tickets/{ticketId}/status")
    public String updateStatus(@PathVariable Integer ticketId,
                               @RequestParam("status") TicketStatus status) {
        dispatcherUserService.updateTicketStatus(ticketId, status);
        return "redirect:/dispatcher/posts";
    }

    @PostMapping("/tickets/{ticketId}/assign")
    public String assignInstitution(@PathVariable Integer ticketId,
                                    @RequestParam("institutionUserId") Integer institutionUserId) {
        dispatcherUserService.assignInstitution(ticketId, institutionUserId);
        return "redirect:/dispatcher/posts";
    }
}