package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "food_logs")
public class FoodLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;        // 自增主键

    @Column(nullable = false)
    private Long userId;    // 对应 users.id

    @Column(nullable = false)
    private String logDate; // 记录日期，例如 "2025-12-08"

    @Column(nullable = false)
    private String foodName;

    @Column(nullable = false)
    private Integer calories;

    // ✅ 用前端生成的稳定 id（跨设备/同步用），旧数据可能为空
    @Column(length = 80)
    private String clientId;

    // ✅ 前端/Android 记录的时间戳（毫秒），旧数据可能为空
    private Long ts;

    // ✅ 可选：图片 URL（用于首页列表），旧数据可能为空
    @Column(length = 512)
    private String img;

    public FoodLog() {}

    public FoodLog(Long userId, String logDate, String foodName, Integer calories) {
        this.userId = userId;
        this.logDate = logDate;
        this.foodName = foodName;
        this.calories = calories;
    }

    public Long getId() { return id; }

    public Long getUserId() { return userId; }

    public void setUserId(Long userId) { this.userId = userId; }

    public String getLogDate() { return logDate; }

    public void setLogDate(String logDate) { this.logDate = logDate; }

    public String getFoodName() { return foodName; }

    public void setFoodName(String foodName) { this.foodName = foodName; }

    public Integer getCalories() { return calories; }

    public void setCalories(Integer calories) { this.calories = calories; }
}
