package com.example.backend.controller;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/food")
public class FoodAiController {

    @PostMapping("/ai-search")
    public Map<String, Object> aiSearch(@RequestBody Map<String, Object> body) {
        String query = String.valueOf(body.getOrDefault("query", "")).trim();

        // 这里先随便回个结果，确认接口正常；之后你再接你的AI逻辑
        return Map.of(
                "ok", true,
                "query", query,
                "answer", "stub"
        );
    }
}
