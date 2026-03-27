package com.raport.app.service;

import com.raport.app.entity.Post;
import com.raport.app.entity.Ticket;
import com.raport.app.entity.User;
import com.raport.app.entity.enums.TicketStatus;
import com.raport.app.entity.enums.UserRole;
import com.raport.app.repository.PostRepository;
import com.raport.app.repository.TicketRepository;
import com.raport.app.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DispatcherUserService {

    private final PostRepository postRepository;
    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    public DispatcherUserService(PostRepository postRepository,
                             TicketRepository ticketRepository,
                             UserRepository userRepository) {
        this.postRepository = postRepository;
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
    }

    public List<Post> getAllPostsForDispatcher() {
        return postRepository.findAllForDispatcher();
    }

    public void takePost(Integer postId, String dispatcherEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Postarea nu a fost găsită."));

        if (ticketRepository.existsByPostId(postId)) {
            throw new IllegalArgumentException("Pentru această postare există deja un ticket.");
        }

        User dispatcher = userRepository.findByEmail(dispatcherEmail)
                .orElseThrow(() -> new IllegalArgumentException("Dispatcherul nu a fost găsit."));

        Ticket ticket = new Ticket();
        ticket.setPost(post);
        ticket.setTicketNumber(generateTicketNumber(post.getId()));
        ticket.setProblemTag(post.getProblemTag());
        ticket.setDispatcherUser(dispatcher);
        ticket.setStatus(TicketStatus.Sesizat);
        ticket.setRankingScore(0);
        ticket.setLastUpdate(LocalDateTime.now());
        ticket.setIsRedFlag(false);

        ticketRepository.save(ticket);
    }

    public void updateTicketStatus(Integer ticketId, TicketStatus newStatus) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticketul nu a fost găsit."));

        ticket.setStatus(newStatus);
        ticket.setLastUpdate(LocalDateTime.now());

        ticketRepository.save(ticket);
    }

    private String generateTicketNumber(Integer postId) {
        return "TCK-" + LocalDateTime.now().getYear() + "-" + postId + "-" + System.currentTimeMillis();
    }

    public void assignInstitution(Integer ticketId, Integer institutionUserId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new IllegalArgumentException("Ticketul nu a fost găsit."));

        User institutionUser = userRepository.findById(institutionUserId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizatorul instituție nu a fost găsit."));

        if (institutionUser.getUserRole() != UserRole.Institution) {
            throw new IllegalArgumentException("Utilizatorul selectat nu este o instituție.");
        }

        ticket.setAssignedInstitutionUser(institutionUser);
        ticket.setLastUpdate(LocalDateTime.now());

        ticketRepository.save(ticket);
    }

    public List<User> getInstitutionUsers() {
        return userRepository.findByUserRole(UserRole.Institution);
    }
}