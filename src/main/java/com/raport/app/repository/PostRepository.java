package com.raport.app.repository;

import com.raport.app.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;


import com.raport.app.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}