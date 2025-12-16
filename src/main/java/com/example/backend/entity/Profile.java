package com.example.backend.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "profiles")
public class Profile {

    @Id
    private Long userId;   // 1:1 对应 users.id

    private Integer height;    // cm
    private Double weight;     // kg
    private Integer age;

    private String sex;        // "M" / "F" / ...
    private String activity;   // 活动水平

    public Profile() {}

    public Profile(Long userId, Integer height, Double weight,
                   Integer age, String sex, String activity) {
        this.userId = userId;
        this.height = height;
        this.weight = weight;
        this.age = age;
        this.sex = sex;
        this.activity = activity;
    }

    // --- getter & setter ---

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Integer getHeight() { return height; }
    public void setHeight(Integer height) { this.height = height; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }

    public Integer getAge() { return age; }
    public void setAge(Integer age) { this.age = age; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public String getActivity() { return activity; }
    public void setActivity(String activity) { this.activity = activity; }
}