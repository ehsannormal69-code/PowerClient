package me.donut.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central store for HUD lines contributed by modules
 * (e.g., finder modules reporting the closest match).
 * Cleared each frame by the HUD renderer pass.
 */
public class HudCollected {
    private static final Map<String, String> LINES = new ConcurrentHashMap<>();

    public static void put(String key, String value) {
        LINES.put(key, value);
    }

    public static Map<String, String> snapshot() {
        return Map.copyOf(LINES);
    }

    public static void clear() {
        LINES.clear();
    }
}
