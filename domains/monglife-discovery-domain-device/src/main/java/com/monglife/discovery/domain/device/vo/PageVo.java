package com.monglife.discovery.domain.device.vo;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/** 페이지 조회 결과 */
@Getter
public class PageVo<T> {

    private final List<T> items;

    private final int page;

    private final int size;

    private final long total;

    @Builder
    public PageVo(List<T> items, int page, int size, long total) {
        this.items = items;
        this.page = page;
        this.size = size;
        this.total = total;
    }

    public int getTotalPage() {
        return size == 0 ? 0 : (int) Math.ceil((double) total / size);
    }

    public boolean isLastPage() {
        return page + 1 >= getTotalPage();
    }
}
