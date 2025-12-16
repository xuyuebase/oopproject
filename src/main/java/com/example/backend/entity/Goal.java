package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "goals")
public class Goal {

    @Id
    private Long userId;       // 和 users.id 一一对应

    @Column(nullable = false)
    private Integer targetCalories;   // 每日目标卡路里

    public Goal() {}

    public Goal(Long userId, Integer targetCalories) {
        this.userId = userId;
        this.targetCalories = targetCalories;
    }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getTargetCalories() { return targetCalories; }

    public void setTargetCalories(Integer targetCalories) {
        this.targetCalories = targetCalories;
    }
}
