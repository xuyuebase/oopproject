package com.example.backend.repo;

import com.example.backend.entity.SocialPost;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SocialPostRepository extends JpaRepository<SocialPost, Long> {
    List<SocialPost> findAllByOrderByCreatedAtDesc();
}
