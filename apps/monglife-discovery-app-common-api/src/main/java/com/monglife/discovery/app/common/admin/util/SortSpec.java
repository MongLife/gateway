package com.monglife.discovery.app.common.admin.util;

import java.util.Set;

/**
 * `sort=field,asc|desc` 파싱. 허용 필드가 아니면 기본값으로 떨어진다.
 */
public record SortSpec(String key, boolean desc) {

    public static SortSpec parse(String raw, Set<String> allowed, String defaultKey, boolean defaultDesc) {
        if (raw == null || raw.isBlank()) return new SortSpec(defaultKey, defaultDesc);
        String[] parts = raw.split(",", 2);
        String key = parts[0].trim();
        if (!allowed.contains(key)) return new SortSpec(defaultKey, defaultDesc);
        boolean desc = parts.length < 2 || !"asc".equalsIgnoreCase(parts[1].trim());
        return new SortSpec(key, desc);
    }
}
