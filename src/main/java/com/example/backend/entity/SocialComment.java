// com.example.backend.entity.SocialComment
package com.example.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="social_comment")
public class SocialComment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public Long postId;
    public Long userId;

    @Column(columnDefinition="TEXT")
    public String text;

    public LocalDateTime createdAt = LocalDateTime.now();
}
