package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepository;

    // 注册：POST /api/auth/signup
    @PostMapping("/signup")
    public Map<String, Object> signup(@RequestBody SignupRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            return Map.of(
                    "success", false,
                    "message", "Email already exists"
            );
        }


        User user = new User(req.getFullName(), req.getEmail(), req.getPassword());
        userRepository.save(user);

        return Map.of(
                "success", true,
                "message", "OK",
                "userId", user.getId(),
                "fullName", user.getFullName(),
                "email", user.getEmail()
        );
    }

    // 登录：POST /api/auth/login
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody LoginRequest req) {
        return userRepository.findByEmail(req.getEmail())
                .filter(u -> u.getPasswordHash().equals(req.getPassword()))
                .<Map<String, Object>>map(u -> Map.of(
                        "success", true,
                        "message", "OK",
                        "userId", u.getId(),
                        "fullName", u.getFullName(),
                        "email", u.getEmail()
                ))
                .orElseGet(() -> Map.of(
                        "success", false,
                        "message", "Wrong email/password"
                ));
    }
    // 修改密码：POST /api/auth/change-password
    @PostMapping("/change-password")
    public Map<String, Object> changePassword(@RequestBody ChangePasswordRequest req) {
        if (req.getUserId() == null) {
            return Map.of("success", false, "message", "Missing userId");
        }
        return userRepository.findById(req.getUserId())
                .<Map<String, Object>>map(u -> {
                    if (req.getOldPassword() == null || !u.getPasswordHash().equals(req.getOldPassword())) {
                        return Map.of("success", false, "message", "Wrong old password");
                    }
                    if (req.getNewPassword() == null || req.getNewPassword().length() < 4) {
                        return Map.of("success", false, "message", "New password too short");
                    }
                    u.setPasswordHash(req.getNewPassword());
                    userRepository.save(u);
                    return Map.of("success", true, "message", "Password updated");
                })
                .orElseGet(() -> Map.of("success", false, "message", "User not found"));
    }

    // 删除账号：POST /api/auth/delete
    @PostMapping("/delete")
    public Map<String, Object> deleteAccount(@RequestBody DeleteAccountRequest req) {
        if (req.getUserId() == null) {
            return Map.of("success", false, "message", "Missing userId");
        }
        return userRepository.findById(req.getUserId())
                .<Map<String, Object>>map(u -> {
                    if (req.getPassword() == null || !u.getPasswordHash().equals(req.getPassword())) {
                        return Map.of("success", false, "message", "Wrong password");
                    }
                    userRepository.delete(u);
                    return Map.of("success", true, "message", "Account deleted");
                })
                .orElseGet(() -> Map.of("success", false, "message", "User not found"));
    }


    // --- 请求体类 ---

    public static class SignupRequest {
        private String fullName;
        private String email;
        private String password;

        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class LoginRequest {
        private String email;
        private String password;

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

    public static class ChangePasswordRequest {
        private Long userId;
        private String oldPassword;
        private String newPassword;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getOldPassword() { return oldPassword; }
        public void setOldPassword(String oldPassword) { this.oldPassword = oldPassword; }
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }

    public static class DeleteAccountRequest {
        private Long userId;
        private String password;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getPassword() { return password; }
        public void setPassword(String password) { this.password = password; }
    }

}
