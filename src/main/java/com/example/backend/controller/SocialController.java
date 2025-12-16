package com.example.backend.controller;

import com.example.backend.entity.Post;
import com.example.backend.entity.PostComment;
import com.example.backend.entity.PostLike;
import com.example.backend.entity.User;
import com.example.backend.repo.PostCommentRepository;
import com.example.backend.repo.PostLikeRepository;
import com.example.backend.repo.PostRepository;
import com.example.backend.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@RestController
@RequestMapping("/api/social")
public class SocialController {

    @Autowired private PostRepository postRepo;
    @Autowired private PostLikeRepository likeRepo;
    @Autowired private PostCommentRepository commentRepo;

    // ✅ 新增：查用户 fullName
    @Autowired private UserRepository userRepo;

    // ====== 小工具：根据 userId 拿 fullName（带缓存，避免重复查库）======
    private String userNameOf(Long uid, Map<Long, String> cache) {
        if (uid == null || uid <= 0) return "Unknown";
        String cached = cache.get(uid);
        if (cached != null) return cached;

        String name = userRepo.findById(uid)
                .map(User::getFullName)
                .orElse("User#" + uid);

        cache.put(uid, name);
        return name;
    }

    // 列出帖子（带点赞数、我是否点赞、评论列表、我是否能删除）
    @GetMapping("/posts")
    public Map<String, Object> listPosts(
            @RequestParam(name = "viewerId", required = false) Long viewerId,
            @RequestParam(name = "userId", required = false) Long userId
    ) {
        long uid = (viewerId != null ? viewerId : (userId != null ? userId : 0L));

        // ✅ 名字缓存（同一次响应内复用）
        Map<Long, String> nameCache = new HashMap<>();

        var posts = postRepo.findLatestAll();

        var arr = posts.stream().map(p -> {
            long postId = p.getId();
            Long ownerIdObj = p.getOwnerId();
            long ownerId = (ownerIdObj == null ? 0L : ownerIdObj);

            boolean likedByMe = (uid > 0) && likeRepo.existsByPostIdAndUserId(postId, uid);
            boolean canDelete = (uid > 0) && ownerId == uid;

            long likeCount = likeRepo.countByPostId(postId);

            // ✅ 评论列表带上 fullName（user 字段给前端显示）
            var comments = commentRepo.findByPostIdOrderByCreatedAtAsc(postId).stream()
                    .map(c -> {
                        long cidOwner = (c.getUserId() == null ? 0L : c.getUserId());
                        boolean cCanDelete = (uid > 0) && (cidOwner == uid);

                        return Map.<String, Object>of(
                                "id", c.getId(),
                                "ownerId", cidOwner,
                                "userId", cidOwner,
                                "user", userNameOf(cidOwner, nameCache),
                                "text", c.getText(),
                                "createdAt", String.valueOf(c.getCreatedAt()),
                                "canDelete", cCanDelete
                        );
                    })
                    .toList();


            String createdAtStr = String.valueOf(p.getCreatedAt());

            return Map.<String, Object>ofEntries(
                    Map.entry("id", postId),
                    Map.entry("ownerId", ownerId),

                    // ✅ 帖子作者显示 fullName
                    Map.entry("user", userNameOf(ownerId, nameCache)),
                    Map.entry("avatar", "https://placehold.co/80x80/eee/000?text=U"),

                    Map.entry("text", p.getText()),
                    Map.entry("tags", p.getTags()),
                    Map.entry("mediaType", p.getMediaType()),
                    Map.entry("mediaUrl", p.getMediaUrl()),

                    Map.entry("createdAt", createdAtStr),
                    Map.entry("time", createdAtStr), // 兼容前端 p.time

                    Map.entry("likeCount", likeCount),
                    Map.entry("likes", likeCount),   // 兼容前端 p.likes
                    Map.entry("likedByMe", likedByMe),

                    Map.entry("commentCount", comments.size()),
                    Map.entry("comments", comments), // 兼容前端 p.comments

                    Map.entry("canDelete", canDelete)
            );
        }).toList();

        return Map.of("ok", true, "posts", arr);
    }

    // 发帖
    @PostMapping("/posts")
    public Map<String, Object> createPost(@RequestBody Map<String, Object> body) {
        long userId = ((Number) body.getOrDefault("userId", 0)).longValue();
        String text = String.valueOf(body.getOrDefault("text", ""));
        String tags = String.valueOf(body.getOrDefault("tags", ""));
        String mediaType = String.valueOf(body.getOrDefault("mediaType", ""));
        String mediaUrl = String.valueOf(body.getOrDefault("mediaUrl", ""));

        if (userId <= 0) return Map.of("ok", false, "msg", "bad userId");

        Post p = new Post();
        p.setOwnerId(userId);
        p.setText(text);
        p.setTags(tags);
        p.setMediaType(mediaType);
        p.setMediaUrl(mediaUrl);

        postRepo.save(p);

        return Map.of("ok", true, "msg", "ok", "postId", p.getId());
    }

    // 删帖：只能删自己的 + 联动删点赞&评论
    @DeleteMapping("/posts/{postId}")
    @Transactional
    public Map<String, Object> deletePost(@PathVariable long postId, @RequestParam long userId) {
        var opt = postRepo.findById(postId);
        if (opt.isEmpty()) return Map.of("ok", false, "msg", "not found");

        Post p = opt.get();
        if (p.getOwnerId() == null || !Objects.equals(p.getOwnerId(), userId)) {
            return Map.of("ok", false, "msg", "not owner");
        }

        likeRepo.deleteByPostId(postId);
        commentRepo.deleteByPostId(postId);
        postRepo.deleteById(postId);

        return Map.of("ok", true, "msg", "deleted");
    }

    // 点赞/取消点赞
    @PostMapping("/posts/{postId}/like")
    public Map<String, Object> toggleLike(@PathVariable long postId, @RequestParam long userId) {
        if (userId <= 0) return Map.of("ok", false, "msg", "bad userId");

        boolean existed = likeRepo.existsByPostIdAndUserId(postId, userId);
        if (existed) {
            likeRepo.deleteByPostIdAndUserId(postId, userId);
        } else {
            likeRepo.save(new PostLike(postId, userId));
        }

        long likeCount = likeRepo.countByPostId(postId);

        return Map.of(
                "ok", true,
                "msg", "ok",
                "likeCount", likeCount,
                "likes", likeCount,     // 兼容前端
                "likedByMe", !existed
        );
    }

    // 评论：拉取某条帖的评论列表
    @GetMapping("/posts/{postId}/comments")
    public Map<String, Object> listComments(@PathVariable long postId,
                                            @RequestParam(required = false, defaultValue = "0") long userId) {

        Map<Long, String> nameCache = new HashMap<>();

        var list = commentRepo.findByPostIdOrderByCreatedAtAsc(postId).stream()
                .map(c -> {
                    long owner = (c.getUserId()==null?0L:c.getUserId());
                    boolean canDelete = (userId > 0) && (owner == userId);
                    return Map.<String, Object>of(
                            "id", c.getId(),
                            "postId", c.getPostId(),
                            "ownerId", owner,
                            "userId", owner,
                            "user", userNameOf(owner, new HashMap<>()),
                            "text", c.getText(),
                            "createdAt", String.valueOf(c.getCreatedAt()),
                            "canDelete", canDelete
                    );
                }) .toList();

        return Map.of("ok", true, "comments", list);
    }

    // 评论：新增评论
    @PostMapping("/posts/{postId}/comments")
    public Map<String, Object> createComment(@PathVariable long postId, @RequestBody Map<String, Object> body) {
        long userId = ((Number) body.getOrDefault("userId", 0)).longValue();
        String text = String.valueOf(body.getOrDefault("text", "")).trim();

        if (userId <= 0) return Map.of("ok", false, "msg", "bad userId");
        if (text.isEmpty()) return Map.of("ok", false, "msg", "empty text");

        PostComment c = new PostComment(postId, userId, text);
        commentRepo.save(c);

        return Map.of("ok", true, "msg", "ok", "commentId", c.getId());
    }

    // 评论：删除评论（只能删自己的）
    @DeleteMapping("/comments/{commentId}")
    @Transactional
    public Map<String, Object> deleteComment(@PathVariable long commentId, @RequestParam long userId) {
        if (userId <= 0) return Map.of("ok", false, "msg", "bad userId");

        long affected = commentRepo.deleteByIdAndUserId(commentId, userId);
        if (affected == 0) return Map.of("ok", false, "msg", "not owner or not found");

        return Map.of("ok", true, "msg", "deleted");
    }
}
