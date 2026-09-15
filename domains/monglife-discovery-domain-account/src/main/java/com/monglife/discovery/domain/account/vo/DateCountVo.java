package com.monglife.discovery.domain.account.vo;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/** 일자별 집계 */
@Getter
public class DateCountVo {

    private final LocalDate date;

    private final long count;

    /** 로그인 통계에서만 쓴다 (distinct 계정 수) */
    private final long uniqueCount;

    @Builder
    public DateCountVo(LocalDate date, long count, long uniqueCount) {
        this.date = date;
        this.count = count;
        this.uniqueCount = uniqueCount;
    }
}
