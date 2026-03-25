package com.raport.app.entity;

import com.raport.app.entity.enums.TicketStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "status_history")
public class StatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;

    @Enumerated(EnumType.STRING)
    @Column(name = "changed_status", nullable = false)
    private TicketStatus changedStatus;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt;

    public Integer getId() {
        return id;
    }

    public Ticket getTicket() {
        return ticket;
    }

    public void setTicket(Ticket ticket) {
        this.ticket = ticket;
    }

    public TicketStatus getChangedStatus() {
        return changedStatus;
    }

    public void setChangedStatus(TicketStatus changedStatus) {
        this.changedStatus = changedStatus;
    }

    public LocalDateTime getChangedAt() {
        return changedAt;
    }

    public void setChangedAt(LocalDateTime changedAt) {
        this.changedAt = changedAt;
    }
}