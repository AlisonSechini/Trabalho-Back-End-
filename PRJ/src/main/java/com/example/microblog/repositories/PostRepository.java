package com.example.microblog.repositories;

import com.example.microblog.entities.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Query("SELECT p FROM Post p LEFT JOIN p.curtidas c GROUP BY p ORDER BY COUNT(c) DESC")
    List<Post> findAllOrderByLikesDesc();

    @Query("SELECT p FROM Post p LEFT JOIN p.curtidas c GROUP BY p ORDER BY COUNT(c) DESC, p.criadoEm DESC")
    List<Post> findPostsOrderByCurtidasDesc();

    @Query("SELECT DISTINCT p FROM Post p LEFT JOIN FETCH p.curtidas ORDER BY p.criadoEm DESC")
    List<Post> findAllChronologicalWithFetch();
}

