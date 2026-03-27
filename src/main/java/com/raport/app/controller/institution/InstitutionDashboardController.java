package com.raport.app.controller.institution;

import com.raport.app.entity.Institution;
import com.raport.app.entity.Ticket;
import com.raport.app.service.institution.InstitutionDashboardService;
import jakarta.servlet.http.HttpSession;
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
            HttpSession session,
            Model model,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search
    ) {
        Integer userId = getLoggedUserId(session);
        if (userId == null) {
            return "redirect:/";
        }

        Institution institution = institutionDashboardService.getInstitutionByUserId(userId);
        List<Ticket> tickets = institutionDashboardService.getTicketsForInstitution(institution.getUserId());

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

//        return institutionDashboardService.getDashboardViewName();
        return rootFolder + "institutionDash.html";
    }


    @GetMapping("/ticket/{id}")
    public String ticketDetail(@PathVariable Integer id,
                               HttpSession session,
                               Model model) {
        Integer userId = getLoggedUserId(session);
        if (userId == null) {
            return "redirect:/";
        }

        Institution institution = institutionDashboardService.getInstitutionByUserId(userId);
        Ticket ticket = institutionDashboardService.getTicketForInstitution(id, institution.getUserId());

        model.addAttribute("institution", institution);
        model.addAttribute("ticket", ticket);

        return institutionDashboardService.getTicketDetailViewName();
    }

    @PostMapping("/ticket/{id}/accept")
    public String acceptTicket(@PathVariable Integer id, HttpSession session) {
        Integer userId = getLoggedUserId(session);
        if (userId == null) {
            return "redirect:/";
        }

        institutionDashboardService.acceptTicket(id, userId);
        return "redirect:/institution/ticket/" + id;
    }

    @PostMapping("/ticket/{id}/in-work")
    public String markInWork(@PathVariable Integer id, HttpSession session) {
        Integer userId = getLoggedUserId(session);
        if (userId == null) {
            return "redirect:/";
        }

        institutionDashboardService.markTicketInWork(id, userId);
        return "redirect:/institution/ticket/" + id;
    }

    @PostMapping("/ticket/{id}/resolve")
    public String resolveTicket(@PathVariable Integer id, HttpSession session) {
        Integer userId = getLoggedUserId(session);
        if (userId == null) {
            return "redirect:/";
        }

        institutionDashboardService.resolveTicket(id, userId);
        return "redirect:/institution/ticket/" + id;
    }


//    ASTA AR TREBUI SA FIE VERSIUNEA CORECTA
    private Integer getLoggedUserId(HttpSession session) {
        Object userIdObj = session.getAttribute("userId");

        if (userIdObj instanceof Integer) {
            return (Integer) userIdObj;
        }
        if (userIdObj instanceof Long) {
            return ((Long) userIdObj).intValue();
        }
        if (userIdObj instanceof String) {
            try {
                return Integer.valueOf((String) userIdObj);
            } catch (NumberFormatException ignored) {
                return null;
            }
        }

        return null;
    }

//    ASTA E DE TEST
//    private Integer getLoggedUserId(HttpSession session) {
//        Object userIdObj = session.getAttribute("userId");
//
//        if (userIdObj instanceof Integer) {
//            return (Integer) userIdObj;
//        }
//        if (userIdObj instanceof Long) {
//            return ((Long) userIdObj).intValue();
//        }
//        if (userIdObj instanceof String) {
//            try {
//                return Integer.valueOf((String) userIdObj);
//            } catch (NumberFormatException ignored) {
//            }
//        }
//
//        // TEMPORAR pentru test local
//        session.setAttribute("userId", 9);
//        return 9;
//    }


    private boolean contains(String value, String q) {
        return value != null && value.toLowerCase().contains(q);
    }
}