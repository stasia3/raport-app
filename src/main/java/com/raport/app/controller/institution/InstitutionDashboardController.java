package com.raport.app.controller.institution;

import com.raport.app.entity.Institution;
import com.raport.app.entity.Ticket;
import com.raport.app.service.institution.InstitutionDashboardService;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/institution")
public class InstitutionDashboardController {
    private final String rootFolder = "app/institution/";

    private final InstitutionDashboardService institutionDashboardService;

    public InstitutionDashboardController(InstitutionDashboardService institutionDashboardService) {
        this.institutionDashboardService = institutionDashboardService;
    }

    @GetMapping("/dashboard")
    public String dashboard(
            Authentication authentication,
            Model model,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search )  {

        String email = authentication.getName();

        Institution institution = institutionDashboardService.getInstitutionByUserEmail(email);
        List<Ticket> tickets = institutionDashboardService.getTicketsForInstitution(institution.getUser().getId());

        if (status != null && !status.isBlank()) {
            tickets = tickets.stream()
                    .filter(ticket -> ticket.getStatus() != null && status.equals(ticket.getStatus().name()))
                    .collect(Collectors.toList());
        }

        if (search != null && !search.isBlank()) {
            String q = search.trim().toLowerCase();
            tickets = tickets.stream()
                    .filter(ticket ->
                            contains(ticket.getTicketNumber(), q) ||
                                    contains(ticket.getProblemTag(), q)
                    )
                    .collect(Collectors.toList());
        }

        model.addAttribute("institution", institution);
        model.addAttribute("tickets", tickets);
        model.addAttribute("newTicketsCount", institutionDashboardService.countNewTickets(tickets));
        model.addAttribute("workingTicketsCount", institutionDashboardService.countWorkingTickets(tickets));
        model.addAttribute("slaTicketsCount", institutionDashboardService.countSlaTickets(tickets));
        model.addAttribute("selectedStatus", status);
        model.addAttribute("searchValue", search);

        return rootFolder + "institution-dash";
    }

    @GetMapping("/ticket/{id}")
    public String ticketDetail(@PathVariable Integer id,
                               Authentication authentication,
                               Model model) {
        String email = authentication.getName();

        Institution institution = institutionDashboardService.getInstitutionByUserEmail(email);
        Ticket ticket = institutionDashboardService.getTicketForInstitution(id, institution.getUser().getId());

        model.addAttribute("institution", institution);
        model.addAttribute("ticket", ticket);
        model.addAttribute("isAuthenticated", true);

        return rootFolder + "institution-ticket-detail";
    }

    @PostMapping("/ticket/{id}/accept")
    public String acceptTicket(@PathVariable Integer id, Authentication authentication) {
        String email = authentication.getName();

        institutionDashboardService.acceptTicket(id, email);
        return "redirect:/institution/ticket/" + id;
    }

    @PostMapping("/ticket/{id}/in-work")
    public String markInWork(@PathVariable Integer id, Authentication authentication) {
        String email = authentication.getName();

        institutionDashboardService.markTicketInWork(id, email);
        return "redirect:/institution/ticket/" + id;
    }

    @PostMapping("/ticket/{id}/resolve")
    public String resolveTicket(@PathVariable Integer id, Authentication authentication) {
        String email = authentication.getName();

        institutionDashboardService.resolveTicket(id, email);
        return "redirect:/institution/ticket/" + id;
    }

    private boolean contains(String value, String q) {
        return value != null && value.toLowerCase().contains(q);
    }
}