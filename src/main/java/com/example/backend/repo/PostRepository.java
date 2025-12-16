package com.example.backend.repo;

import com.example.backend.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findTop50ByOrderByCreatedAtDesc();

    @Query("select p from Post p order by p.createdAt desc")
    List<Post> findLatestAll();
}
