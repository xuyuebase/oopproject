package com.example.backend.controller;

import com.example.backend.entity.Profile;
import com.example.backend.repo.ProfileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    @Autowired
    private ProfileRepository profileRepository;

    // POST /api/profile/save
    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody ProfileRequest req) {
        Profile profile = new Profile(
                req.getUserId(),
                req.getHeight(),
                req.getWeight(),
                req.getAge(),
                req.getSex(),
                req.getActivity()
        );
        profileRepository.save(profile);
        return Map.of("ok", true, "msg", "Saved");
    }

    // ✅ 改成：POST /api/profile/get
    // Android 的 BackendApiClient.getProfileRemote 会调用它
    @PostMapping("/get")
    public Map<String, Object> get(@RequestBody ProfileRequest req) {
        Long userId = req.getUserId();
        if (userId == null) {
            // 没传 userId 就返回空对象
            return Map.of();
        }

        return profileRepository.findById(userId)
                .<Map<String, Object>>map(p -> Map.of(
                        "height", p.getHeight(),
                        "weight", p.getWeight(),
                        "age", p.getAge(),
                        "sex", p.getSex(),
                        "activity", p.getActivity()
                ))
                .orElseGet(Map::of);
    }

    // --- 接收前端 JSON 的类 ---
    public static class ProfileRequest {
        private Long userId;
        private Integer height;
        private Double weight;
        private Integer age;
        private String sex;
        private String activity;

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
}

