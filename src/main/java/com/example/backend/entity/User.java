package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;          // userId

    @Column(nullable = false)
    private String fullName;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String passwordHash;   // 暂时直接存密码，之后可以改成加密

    // ✅ 新增：每日目标（单位 kcal），可以为 null
    private Integer dailyGoalKcal;

    public User() {}

    public User(String fullName, String email, String passwordHash) {
        this.fullName = fullName;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // --- getter / setter ---

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    // ✅ 新增 getter / setter
    public Integer getDailyGoalKcal() {
        return dailyGoalKcal;
    }

    public void setDailyGoalKcal(Integer dailyGoalKcal) {
        this.dailyGoalKcal = dailyGoalKcal;
    }
}
