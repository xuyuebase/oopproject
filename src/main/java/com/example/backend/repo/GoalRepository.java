package com.example.backend.repo;

import com.example.backend.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    // 这里暂时不用额外方法，findById(userId) 就行
}
