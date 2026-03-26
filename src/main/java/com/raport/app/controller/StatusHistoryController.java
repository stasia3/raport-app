package com.raport.app.controller;

import com.raport.app.entity.StatusHistory;
import com.raport.app.entity.Ticket;
import com.raport.app.entity.enums.TicketStatus;
import com.raport.app.repository.StatusHistoryRepository;
import com.raport.app.repository.TicketRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@RequestMapping("/admin/status-history")
public class StatusHistoryController {
    private final String rootFolder = "admin/db-tables/";

    private final StatusHistoryRepository statusHistoryRepository;
    private final TicketRepository ticketRepository;

    public StatusHistoryController(StatusHistoryRepository statusHistoryRepository,
                                   TicketRepository ticketRepository) {
        this.statusHistoryRepository = statusHistoryRepository;
        this.ticketRepository = ticketRepository;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("histories", statusHistoryRepository.findAll());
        return rootFolder + "status-history-list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("statusHistory", new StatusHistory());
        model.addAttribute("tickets", ticketRepository.findAll());
        model.addAttribute("statuses", TicketStatus.values());
        return rootFolder + "status-history-form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute("statusHistory") StatusHistory statusHistory,
                       @RequestParam("ticketId") Integer ticketId,
                       Model model) {

        Optional<Ticket> ticketOptional = ticketRepository.findById(ticketId);

        if (ticketOptional.isEmpty()) {
            model.addAttribute("error", "Ticket not found");
            model.addAttribute("tickets", ticketRepository.findAll());
            model.addAttribute("statuses", TicketStatus.values());
            return rootFolder + "status-history-form";
        }

        statusHistory.setTicket(ticketOptional.get());

        if (statusHistory.getId() == null) {
            statusHistory.setChangedAt(LocalDateTime.now());
        } else {
            StatusHistory existing = statusHistoryRepository.findById(statusHistory.getId()).orElse(null);
            if (existing != null) {
                statusHistory.setChangedAt(existing.getChangedAt());
            }
        }

        statusHistoryRepository.save(statusHistory);
        return "redirect:/admin/status-history";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Integer id, Model model) {
        Optional<StatusHistory> optional = statusHistoryRepository.findById(id);

        if (optional.isPresent()) {
            model.addAttribute("statusHistory", optional.get());
            model.addAttribute("tickets", ticketRepository.findAll());
            model.addAttribute("statuses", TicketStatus.values());
            return rootFolder + "status-history-form";
        }

        return "redirect:/admin/status-history";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        statusHistoryRepository.deleteById(id);
        return "redirect:/admin/status-history";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Integer id, Model model) {
        Optional<StatusHistory> optional = statusHistoryRepository.findById(id);

        if (optional.isPresent()) {
            model.addAttribute("statusHistory", optional.get());
            return rootFolder + "status-history-view";
        }

        return "redirect:/admin/status-history";
    }
}