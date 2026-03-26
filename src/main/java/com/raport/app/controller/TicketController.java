package com.raport.app.controller;

import com.raport.app.entity.Post;
import com.raport.app.entity.Ticket;
import com.raport.app.entity.User;
import com.raport.app.entity.enums.TicketStatus;
import com.raport.app.repository.PostRepository;
import com.raport.app.repository.TicketRepository;
import com.raport.app.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/admin/tickets")
public class TicketController {
    private final String rootFolder = "admin/db-tables/";

    private final TicketRepository ticketRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public TicketController(TicketRepository ticketRepository,
                            PostRepository postRepository,
                            UserRepository userRepository) {
        this.ticketRepository = ticketRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String listTickets(Model model) {
        model.addAttribute("tickets", ticketRepository.findAll());
        return rootFolder + "ticket-list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        Ticket ticket = new Ticket();
        ticket.setStatus(TicketStatus.Sesizat);
        ticket.setRankingScore(0);
        ticket.setIsRedFlag(false);

        model.addAttribute("ticket", ticket);
        model.addAttribute("posts", postRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        model.addAttribute("statuses", TicketStatus.values());
        return rootFolder + "ticket-form";
    }

    @PostMapping("/save")
    public String saveTicket(@ModelAttribute("ticket") Ticket ticket,
                             @RequestParam("postId") Integer postId,
                             @RequestParam(value = "assignedInstitutionUserId", required = false) Integer assignedInstitutionUserId,
                             @RequestParam(value = "dispatcherUserId", required = false) Integer dispatcherUserId,
                             Model model) {

        Optional<Post> postOptional = postRepository.findById(postId);

        if (postOptional.isEmpty()) {
            model.addAttribute("error", "Post not found");
            model.addAttribute("posts", postRepository.findAll());
            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("statuses", TicketStatus.values());
            return rootFolder + "ticket-form";
        }

        ticket.setPost(postOptional.get());

        if (assignedInstitutionUserId != null) {
            Optional<User> assignedUserOptional = userRepository.findById(assignedInstitutionUserId);
            ticket.setAssignedInstitutionUser(assignedUserOptional.orElse(null));
        } else {
            ticket.setAssignedInstitutionUser(null);
        }

        if (dispatcherUserId != null) {
            Optional<User> dispatcherUserOptional = userRepository.findById(dispatcherUserId);
            ticket.setDispatcherUser(dispatcherUserOptional.orElse(null));
        } else {
            ticket.setDispatcherUser(null);
        }

        if (ticket.getStatus() == null) {
            ticket.setStatus(TicketStatus.Sesizat);
        }

        if (ticket.getRankingScore() == null) {
            ticket.setRankingScore(0);
        }

        if (ticket.getIsRedFlag() == null) {
            ticket.setIsRedFlag(false);
        }

        ticket.setLastUpdate(LocalDateTime.now());

        ticketRepository.save(ticket);
        return "redirect:/admin/tickets";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Integer id, Model model) {
        Optional<Ticket> ticketOptional = ticketRepository.findById(id);

        if (ticketOptional.isPresent()) {
            model.addAttribute("ticket", ticketOptional.get());
            model.addAttribute("posts", postRepository.findAll());
            model.addAttribute("users", userRepository.findAll());
            model.addAttribute("statuses", TicketStatus.values());
            return rootFolder + "ticket-form";
        }

        return "redirect:/admin/tickets";
    }

    @GetMapping("/view/{id}")
    public String viewTicket(@PathVariable Integer id, Model model) {
        Optional<Ticket> ticketOptional = ticketRepository.findById(id);

        if (ticketOptional.isPresent()) {
            model.addAttribute("ticket", ticketOptional.get());
            return rootFolder + "ticket-view";
        }

        return "redirect:/admin/tickets";
    }

    @GetMapping("/delete/{id}")
    public String deleteTicket(@PathVariable Integer id) {
        ticketRepository.deleteById(id);
        return "redirect:/admin/tickets";
    }
}