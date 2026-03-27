package com.raport.app.service;

import com.raport.app.entity.Post;
import com.raport.app.entity.enums.TicketStatus;
import com.raport.app.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FeedService {

    private final PostRepository postRepository;

    public FeedService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public List<Post> getPostsForFeed(String status) {
        if (status == null || status.isBlank() || status.equalsIgnoreCase("toate")) {
            return postRepository.findAllForFeed();
        }

        TicketStatus ticketStatus = TicketStatus.valueOf(status);
        return postRepository.findAllForFeedByTicketStatus(ticketStatus);
    }
}