package com.example.backend.repo;

import com.example.backend.entity.PostComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface PostCommentRepository extends JpaRepository<PostComment, Long> {

    List<PostComment> findByPostIdOrderByCreatedAtAsc(Long postId);

    long countByPostId(Long postId);
    @Transactional
    @Modifying
    void deleteByPostId(Long postId);

    @Transactional
    @Modifying
    // 用来保证“只能删自己的评论”
    long deleteByIdAndUserId(Long id, Long userId);
}
