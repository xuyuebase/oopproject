package com.example.backend.controller;

import com.example.backend.entity.FoodLog;
import com.example.backend.entity.Goal;
import com.example.backend.repo.FoodLogRepository;
import com.example.backend.repo.GoalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/api")
public class GoalFoodController {

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private FoodLogRepository foodLogRepository;

    @PersistenceContext
    private EntityManager em;

    private static final Pattern DATE_RE = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    // 1) 保存 / 更新目标：POST /api/goals/save
    @PostMapping("/goals/save")
    public Map<String, Object> saveGoal(@RequestBody GoalRequest req) {
        if (req == null || req.getUserId() == null || req.getTargetCalories() == null) {
            return Map.of("ok", false, "msg", "Missing userId/targetCalories");
        }
        Goal goal = new Goal(req.getUserId(), req.getTargetCalories());
        goalRepository.save(goal);   // userId 相同会变成更新
        return Map.of("ok", true, "msg", "Goal saved");
    }

    // 2) 添加食物记录：POST /api/food/add
    //    兼容旧版字段：userId/date/foodName/calories
    @PostMapping("/food/add")
    public Map<String, Object> addFood(@RequestBody FoodLogRequest req) {
        if (req == null || req.getUserId() == null) {
            return Map.of("ok", false, "msg", "Missing userId");
        }
        String date = (req.getDate() == null ? "" : req.getDate().trim());
        if (!isValidDate(date)) {
            return Map.of("ok", false, "msg", "Invalid date, expected yyyy-MM-dd");
        }

        String name = (req.getFoodName() == null ? "" : req.getFoodName().trim());
        if (name.isEmpty()) name = "Food";

        Integer cal = req.getCalories();
        if (cal == null) cal = estimateCaloriesByName(name);

        FoodLog log = new FoodLog(req.getUserId(), date, name, cal);
        FoodLog saved = foodLogRepository.save(log);

        return Map.of(
                "ok", true,
                "msg", "Food added",
                "id", saved.getId(),
                "item", Map.of(
                        "id", saved.getId(),
                        "userId", saved.getUserId(),
                        "date", saved.getLogDate(),
                        "foodName", saved.getFoodName(),
                        "calories", saved.getCalories()
                )
        );
    }

    // 3) 获取当天汇总：POST /api/summary/get
    //    ★ 保持你的原返回字段名：intakeToday / goalDaily
    @PostMapping("/summary/get")
    public Map<String, Object> getSummary(@RequestBody SummaryRequest req) {

        Long userId = (req == null ? null : req.getUserId());
        String date = (req == null ? null : req.getDate());

        if (userId == null || date == null || !isValidDate(date)) {
            return Map.of("ok", false, "msg", "Missing/invalid userId/date");
        }

        List<FoodLog> logs = foodLogRepository.findByUserIdAndLogDate(userId, date);
        int total = 0;
        for (FoodLog l : logs) {
            if (l.getCalories() != null) total += l.getCalories();
        }

        Integer target = null;
        if (goalRepository.findById(userId).isPresent()) {
            target = goalRepository.findById(userId).get().getTargetCalories();
        }

        return Map.of(
                "intakeToday", total,
                "goalDaily", target
        );
    }

    // =========================
    // ✅ 新增：后端为真相的读取/同步接口
    // =========================

    // 4) 拉某天食物列表：POST /api/food/listByDate
    @PostMapping("/food/listByDate")
    public Map<String, Object> listByDate(@RequestBody ListByDateRequest req) {
        if (req == null || req.getUserId() == null) {
            return Map.of("ok", false, "msg", "Missing userId");
        }
        String date = (req.getDate() == null ? "" : req.getDate().trim());
        if (!isValidDate(date)) {
            return Map.of("ok", false, "msg", "Invalid date, expected yyyy-MM-dd");
        }

        List<FoodLog> logs = foodLogRepository.findByUserIdAndLogDate(req.getUserId(), date);
        // 你原 repo 没有 orderBy；这里用 id 倒序模拟“最近在上”
        logs.sort((a, b) -> Long.compare(
                (b.getId() == null ? 0L : b.getId()),
                (a.getId() == null ? 0L : a.getId())
        ));

        return Map.of("ok", true, "items", toItems(logs));
    }

    // 5) 拉区间（用于登录后同步）：POST /api/food/listRange
    //    说明：logDate 是 yyyy-MM-dd 字符串，按字典序 between 可用
    @PostMapping("/food/listRange")
    public Map<String, Object> listRange(@RequestBody ListRangeRequest req) {
        if (req == null || req.getUserId() == null) {
            return Map.of("ok", false, "msg", "Missing userId");
        }
        String start = (req.getStartDate() == null ? "" : req.getStartDate().trim());
        String end = (req.getEndDate() == null ? "" : req.getEndDate().trim());
        if (!isValidDate(start) || !isValidDate(end)) {
            return Map.of("ok", false, "msg", "Invalid startDate/endDate, expected yyyy-MM-dd");
        }

        List<FoodLog> logs = em.createQuery(
                        "SELECT f FROM FoodLog f " +
                                "WHERE f.userId = :uid AND f.logDate BETWEEN :s AND :e " +
                                "ORDER BY f.logDate DESC, f.id DESC",
                        FoodLog.class
                )
                .setParameter("uid", req.getUserId())
                .setParameter("s", start)
                .setParameter("e", end)
                .getResultList();

        return Map.of("ok", true, "items", toItems(logs));
    }

    // 6) 查一条：POST /api/food/getOne
    @PostMapping("/food/getOne")
    public Map<String, Object> getOne(@RequestBody GetOneRequest req) {
        if (req == null || req.getUserId() == null || req.getId() == null) {
            return Map.of("ok", false, "msg", "Missing userId/id");
        }

        Optional<FoodLog> opt = foodLogRepository.findById(req.getId());
        if (opt.isEmpty()) {
            return Map.of("ok", false, "msg", "Not found");
        }

        FoodLog log = opt.get();
        if (!Objects.equals(log.getUserId(), req.getUserId())) {
            return Map.of("ok", false, "msg", "Forbidden");
        }

        return Map.of("ok", true, "item", toItem(log));
    }

    // 7) 更新一条：POST /api/food/update
    @PostMapping("/food/update")
    public Map<String, Object> update(@RequestBody UpdateFoodRequest req) {
        if (req == null || req.getUserId() == null || req.getId() == null) {
            return Map.of("ok", false, "msg", "Missing userId/id");
        }

        Optional<FoodLog> opt = foodLogRepository.findById(req.getId());
        if (opt.isEmpty()) {
            return Map.of("ok", false, "msg", "Not found");
        }

        FoodLog log = opt.get();
        if (!Objects.equals(log.getUserId(), req.getUserId())) {
            return Map.of("ok", false, "msg", "Forbidden");
        }

        if (req.getDate() != null) {
            String d = req.getDate().trim();
            if (!d.isEmpty()) {
                if (!isValidDate(d)) return Map.of("ok", false, "msg", "Invalid date");
                log.setLogDate(d);
            }
        }
        if (req.getFoodName() != null) {
            String n = req.getFoodName().trim();
            if (!n.isEmpty()) log.setFoodName(n);
        }
        if (req.getCalories() != null) {
            log.setCalories(req.getCalories());
        }

        FoodLog saved = foodLogRepository.save(log);
        return Map.of("ok", true, "item", toItem(saved));
    }

    // 8) 删除一条：POST /api/food/delete
    @PostMapping("/food/delete")
    public Map<String, Object> delete(@RequestBody DeleteFoodRequest req) {
        if (req == null || req.getUserId() == null || req.getId() == null) {
            return Map.of("ok", false, "msg", "Missing userId/id");
        }

        Optional<FoodLog> opt = foodLogRepository.findById(req.getId());
        if (opt.isEmpty()) {
            return Map.of("ok", true, "msg", "Already deleted");
        }

        FoodLog log = opt.get();
        if (!Objects.equals(log.getUserId(), req.getUserId())) {
            return Map.of("ok", false, "msg", "Forbidden");
        }

        foodLogRepository.deleteById(req.getId());
        return Map.of("ok", true, "msg", "Deleted");
    }

    // ==================
    // 你原本就有的（保留）
    // ==================

    // 一个简单的“估算”方法，先随便写几个例子（你原本的逻辑保留）
    private int estimateCaloriesByName(String foodName) {
        String f = (foodName == null ? "" : foodName).toLowerCase();

        if (f.contains("fried rice")) return 600;  // 炒饭
        if (f.contains("rice")) return 300;        // 一碗饭
        if (f.contains("noodle") || f.contains("ramen")) return 500;
        if (f.contains("bread")) return 250;
        if (f.contains("apple")) return 80;
        if (f.contains("banana")) return 100;
        if (f.contains("milk tea")) return 450;
        if (f.contains("cola") || f.contains("coke")) return 150;

        // 默认值，假设普通一份食物 300 kcal
        return 300;
    }

    private boolean isValidDate(String s) {
        return s != null && DATE_RE.matcher(s).matches();
    }

    private List<Map<String, Object>> toItems(List<FoodLog> logs) {
        List<Map<String, Object>> out = new ArrayList<>();
        if (logs == null) return out;
        for (FoodLog l : logs) out.add(toItem(l));
        return out;
    }

    private Map<String, Object> toItem(FoodLog l) {
        return Map.of(
                "id", l.getId(),
                "userId", l.getUserId(),
                "date", l.getLogDate(),
                "foodName", l.getFoodName(),
                "calories", l.getCalories()
        );
    }

    // ==================
    // 请求体类（保留 + 新增）
    // ==================

    public static class GoalRequest {
        private Long userId;
        private Integer targetCalories;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public Integer getTargetCalories() { return targetCalories; }
        public void setTargetCalories(Integer targetCalories) { this.targetCalories = targetCalories; }
    }

    // 你原来的 addFood 用这个（保留）
    public static class FoodLogRequest {
        private Long userId;
        private String date;      // ★ 字段名 date（与你前端 JSON 对上）
        private String foodName;
        private Integer calories;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public String getFoodName() { return foodName; }
        public void setFoodName(String foodName) { this.foodName = foodName; }

        public Integer getCalories() { return calories; }
        public void setCalories(Integer calories) { this.calories = calories; }
    }

    // 你原来的 summary 用这个（保留）
    public static class SummaryRequest {
        private Long userId;
        private String date;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
    }

    // ===== 新增：listByDate =====
    public static class ListByDateRequest {
        private Long userId;
        private String date;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
    }

    // ===== 新增：listRange =====
    public static class ListRangeRequest {
        private Long userId;
        private String startDate;
        private String endDate;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getStartDate() { return startDate; }
        public void setStartDate(String startDate) { this.startDate = startDate; }

        public String getEndDate() { return endDate; }
        public void setEndDate(String endDate) { this.endDate = endDate; }
    }

    // ===== 新增：getOne =====
    public static class GetOneRequest {
        private Long userId;
        private Long id;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    // ===== 新增：update =====
    public static class UpdateFoodRequest {
        private Long userId;
        private Long id;
        private String date;       // 可选
        private String foodName;   // 可选
        private Integer calories;  // 可选

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }

        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }

        public String getFoodName() { return foodName; }
        public void setFoodName(String foodName) { this.foodName = foodName; }

        public Integer getCalories() { return calories; }
        public void setCalories(Integer calories) { this.calories = calories; }
    }

    // ===== 新增：delete =====
    public static class DeleteFoodRequest {
        private Long userId;
        private Long id;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
    }

    // ======= 你文件里已有的 AI 请求体（保留，不影响） =======
    public static class AnalyzeFoodRequest {
        private Long userId;
        private String foodName;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }

        public String getFoodName() { return foodName; }
        public void setFoodName(String foodName) { this.foodName = foodName; }
    }
}
