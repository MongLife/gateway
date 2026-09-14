package com.monglife.discovery.app.common.admin.util;

import com.monglife.core.dto.response.PageResponseDto;
import com.monglife.core.enums.response.Response;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.function.Function;

/**
 * 페이지 응답. PageResponseDto(page,size,totalPage,isLastPage) + X-Total-Count 헤더.
 * 도메인마다 PageVo 가 따로 있어 필드만 받는다.
 */
public record AdminPage<T>(List<T> items, int page, int size, long total) {

    public static final String TOTAL_COUNT_HEADER = "X-Total-Count";

    public <R> AdminPage<R> map(Function<T, R> mapper) {
        return new AdminPage<>(items.stream().map(mapper).toList(), page, size, total);
    }

    public int totalPage() {
        return size == 0 ? 0 : (int) Math.ceil((double) total / size);
    }

    public boolean isLastPage() {
        return page + 1 >= totalPage();
    }

    public ResponseEntity<PageResponseDto<List<T>>> toResponse(Response response) {
        return ResponseEntity.ok()
                .header(TOTAL_COUNT_HEADER, String.valueOf(total))
                .body(response.toPageResponseDto(items, page, size, totalPage(), isLastPage()));
    }
}
