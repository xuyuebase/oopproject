// com.example.backend.entity.SocialPost
package com.example.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name="social_post")
public class SocialPost {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long id;

    public Long ownerId;

    @Column(columnDefinition = "TEXT")
    public String text;

    public String tags;
    public String mediaType; // image / video / ""

    @Lob
    @Column(columnDefinition = "LONGTEXT")
    public String mediaUrl;  // dataURL/base64（先这样，后续可换对象存储）

    public LocalDateTime createdAt = LocalDateTime.now();
}
