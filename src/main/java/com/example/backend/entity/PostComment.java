package com.example.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "post_comment")
public class PostComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="post_id", nullable=false)
    private Long postId;

    @Column(name="user_id", nullable=false)
    private Long userId;

    @Column(name="text", nullable=false, length = 1000)
    private String text;

    @Column(name="created_at", nullable=false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public PostComment() {}

    public PostComment(Long postId, Long userId, String text) {
        this.postId = postId;
        this.userId = userId;
        this.text = text;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getPostId() { return postId; }
    public Long getUserId() { return userId; }
    public String getText() { return text; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setPostId(Long postId) { this.postId = postId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setText(String text) { this.text = text; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
