package com.raport.app.repository.institution;

import com.raport.app.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TicketDashRepository extends JpaRepository<Ticket, Integer> {

    List<Ticket> findByAssignedInstitutionUser_Id(Integer assignedInstitutionUserId);

    Optional<Ticket> findByIdAndAssignedInstitutionUser_Id(Integer id, Integer assignedInstitutionUserId);
}