package com.raport.app.repository;

import com.raport.app.entity.Ticket;
import com.raport.app.entity.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {

    Optional<Ticket> findByPostId(Integer postId);

    boolean existsByPostId(Integer postId);

    List<Ticket> findByStatus(TicketStatus status);
}