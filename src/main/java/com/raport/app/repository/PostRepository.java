package com.raport.app.repository;

import com.raport.app.entity.Post;
import com.raport.app.entity.enums.TicketStatus;
import org.springframework.data.jpa.repository.JpaRepository;


import com.raport.app.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Integer> {

    @Query("""
        select distinct p
        from Post p
        left join fetch p.photos
        left join fetch p.ticket
        left join fetch p.creator
        order by p.createdAt desc
    """)
    List<Post> findAllForDispatcher();

    @Query("""
        select distinct p
        from Post p
        left join fetch p.photos
        left join fetch p.creator
        left join fetch p.ticket t
        order by p.createdAt desc
    """)
    List<Post> findAllForFeed();

    @Query("""
        select distinct p
        from Post p
        left join fetch p.photos
        left join fetch p.creator
        left join fetch p.ticket t
        where t.status = :status
        order by p.createdAt desc
    """)
    List<Post> findAllForFeedByTicketStatus(@Param("status") TicketStatus status);
    @Query("""
    select distinct p
    from Post p
    left join fetch p.photos
    left join fetch p.ticket
    left join fetch p.creator c
    left join fetch c.user
    where c.user.email = :email
    order by p.createdAt desc
""")
    List<Post> findAllByCreatorEmail(@Param("email") String email);

}