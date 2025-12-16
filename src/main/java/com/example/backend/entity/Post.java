package com.example.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 发帖人（你的 User 表主键）
    @Column(nullable = false)
    private Long ownerId;

    @Column(columnDefinition = "TEXT")
    private String text;

    @Column(columnDefinition = "TEXT")
    private String tags;

    // image / video / ""
    private String mediaType;

    // ✅ 最简单：直接存 dataURL 或 URL（推荐后续改 URL）
    // 比如 "data:image/jpeg;base64,...." 或 "https://..."
    @Column(columnDefinition = "LONGTEXT")
    private String mediaUrl;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getTags() { return tags; }
    public void setTags(String tags) { this.tags = tags; }

    public String getMediaType() { return mediaType; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }

    public String getMediaUrl() { return mediaUrl; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
