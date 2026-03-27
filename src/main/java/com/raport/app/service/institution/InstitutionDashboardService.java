package com.raport.app.service.institution;

import com.raport.app.entity.Institution;
import com.raport.app.entity.Ticket;
import com.raport.app.entity.enums.TicketStatus;
import com.raport.app.repository.institution.InstitutionDashRepository;
import com.raport.app.repository.institution.TicketDashRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class InstitutionDashboardService {

    private final InstitutionDashRepository institutionDashRepository;
    private final TicketDashRepository ticketDashRepository;

    public InstitutionDashboardService(InstitutionDashRepository institutionDashRepository,
                                       TicketDashRepository ticketDashRepository) {
        this.institutionDashRepository = institutionDashRepository;
        this.ticketDashRepository = ticketDashRepository;
    }

    public String getDashboardViewName() {
        return "app.institutii/institutionDash";
    }

    public String getTicketDetailViewName() {
        return "app.institutii/institutionTicketDetail";
    }

    public Institution getInstitutionByUserId(Integer userId) {
        return institutionDashRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Nu exista institution pentru userId-ul: " + userId
                ));
    }

    public List<Ticket> getTicketsForInstitution(Integer institutionUserId) {
        return ticketDashRepository.findByAssignedInstitutionUser_Id(institutionUserId)
                .stream()
                .sorted(Comparator.comparing(Ticket::getLastUpdate).reversed())
                .toList();
    }

    public Ticket getTicketForInstitution(Integer ticketId, Integer institutionUserId) {
        return ticketDashRepository.findByIdAndAssignedInstitutionUser_Id(ticketId, institutionUserId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Tichetul nu exista sau nu apartine institutiei logate."
                ));
    }

    public long countNewTickets(List<Ticket> tickets) {
        return tickets.stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.Trimis)
                .count();
    }

    public long countWorkingTickets(List<Ticket> tickets) {
        return tickets.stream()
                .filter(ticket -> ticket.getStatus() == TicketStatus.In_Lucru)
                .count();
    }

    public long countSlaTickets(List<Ticket> tickets) {
        return tickets.stream()
                .filter(ticket -> Boolean.TRUE.equals(ticket.getIsRedFlag()))
                .count();
    }

    @Transactional
    public void acceptTicket(Integer ticketId, Integer institutionUserId) {
        Ticket ticket = getTicketForInstitution(ticketId, institutionUserId);
        ticket.setStatus(TicketStatus.Preluat);
        ticketDashRepository.save(ticket);
    }

    @Transactional
    public void markTicketInWork(Integer ticketId, Integer institutionUserId) {
        Ticket ticket = getTicketForInstitution(ticketId, institutionUserId);
        ticket.setStatus(TicketStatus.In_Lucru);
        ticketDashRepository.save(ticket);
    }

    @Transactional
    public void resolveTicket(Integer ticketId, Integer institutionUserId) {
        Ticket ticket = getTicketForInstitution(ticketId, institutionUserId);
        ticket.setStatus(TicketStatus.Rezolvat);
        ticketDashRepository.save(ticket);
    }
}