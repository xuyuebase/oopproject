package com.example.backend.ai;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AiFoodService {

    // 离线版本：后端不做AI，直接返回占位
    public Map<String, Object> aiSearch(String query) {
        return Map.of(
                "ok", true,
                "mode", "offline",
                "query", query,
                "answer", "Local TFLite on Android handles recognition; backend AI disabled."
        );
    }
}
