package com.example.backend.repo;

import com.example.backend.entity.FoodLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FoodLogRepository extends JpaRepository<FoodLog, Long> {

    // 根据用户 + 日期查所有记录，用来算当天总卡路里
    List<FoodLog> findByUserIdAndLogDate(Long userId, String logDate);
    // ✅ 同一天按时间戳倒序（用于列表）
    List<FoodLog> findByUserIdAndLogDateOrderByTsDesc(Long userId, String logDate);

    // ✅ 拉区间（用于登录后同步）
    List<FoodLog> findByUserIdAndLogDateBetween(Long userId, String startDate, String endDate);

    // ✅ 用 clientId 精确定位（用于 update/delete/getOne）
    FoodLog findFirstByUserIdAndClientId(Long userId, String clientId);
}
