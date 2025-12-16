// com.example.backend.entity.SocialPostLike
package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name="social_post_like",
        uniqueConstraints = @UniqueConstraint(columnNames={"postId","userId"}))
public class SocialPostLike {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public Long postId;
    public Long userId;
}
