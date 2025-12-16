package com.example.backend.repo;

import com.example.backend.entity.SocialPostLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SocialPostLikeRepository extends JpaRepository<SocialPostLike, Long> {
    boolean existsByPostIdAndUserId(Long postId, Long userId);
    long countByPostId(Long postId);
    void deleteByPostId(Long postId);
    void deleteByPostIdAndUserId(Long postId, Long userId);
}
