package com.example.backend.controller;

import com.example.backend.entity.User;
import com.example.backend.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")   // ✅ 统一前缀：/api/user/...
public class UserController {

    @Autowired
    private UserRepository userRepository;

    // 1) 保存目标：POST /api/user/goal
    @PostMapping("/goal")       // ✅ 现在是 /api/user/goal，不是 /api/goals/save
    public Map<String, Object> saveGoal(@RequestBody SaveGoalRequest req) {
        User u = userRepository.findById((long) req.getUserId()).orElse(null);
        if (u == null) {
            return Map.of("success", false, "message", "User not found");
        }

        u.setDailyGoalKcal(req.getGoalKcal());
        userRepository.save(u);

        return Map.of("success", true, "message", "Goal updated");
    }

    // 2) 获取 summary：GET /api/user/summary/{userId}
    @GetMapping("/summary/{userId}")
    public Map<String, Object> getSummary(@PathVariable int userId) {
        User u = userRepository.findById((long) userId).orElse(null);
        if (u == null) {
            return Map.of(
                    "intakeToday", 0,
                    "goalDaily", 0
            );
        }

        Integer goal = u.getDailyGoalKcal();
        if (goal == null) goal = 0;

        return Map.of(
                "intakeToday", 0,   // 以后你接食物表再改这里
                "goalDaily", goal
        );
    }

    // ---- 请求体类 ----
    public static class SaveGoalRequest {
        private int userId;
        private int goalKcal;

        public int getUserId() { return userId; }
        public void setUserId(int userId) { this.userId = userId; }

        public int getGoalKcal() { return goalKcal; }
        public void setGoalKcal(int goalKcal) { this.goalKcal = goalKcal; }
    }
}
