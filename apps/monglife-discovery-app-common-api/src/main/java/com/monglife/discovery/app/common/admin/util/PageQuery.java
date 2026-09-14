package com.monglife.discovery.app.common.admin.util;

/** page/size 쿼리 파라미터 정규화. size 상한 100 */
public final class PageQuery {

    public static final int DEFAULT_SIZE = 15;
    public static final int MAX_SIZE = 100;

    private PageQuery() {}

    public static int page(Integer page) {
        return page == null || page < 0 ? 0 : page;
    }

    public static int size(Integer size) {
        if (size == null || size < 1) return DEFAULT_SIZE;
        return Math.min(size, MAX_SIZE);
    }

    /** 빈 문자열은 null 로 (필터 "전체") */
    public static String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
