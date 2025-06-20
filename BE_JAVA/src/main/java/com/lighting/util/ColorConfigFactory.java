package com.lighting.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ColorConfigFactory {
    private static final Logger log = LoggerFactory.getLogger(ColorConfigFactory.class);
    private static final Map<String, ColorConfig> colorCache = new HashMap<>();

    public static ColorConfig getColorConfig(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null; // Cho phép màu null vì không có @NotBlank trong Order.OrderItem
        }
        String key = name.toLowerCase();
        ColorConfig config = colorCache.computeIfAbsent(key, k -> {
            log.info("Tạo ColorConfig mới: {}", name);
            return new ColorConfig(name);
        });
        log.info("Tái sử dụng ColorConfig: {}", name);
        return config;
    }

    public static int getCacheSize() {
        return colorCache.size();
    }
}