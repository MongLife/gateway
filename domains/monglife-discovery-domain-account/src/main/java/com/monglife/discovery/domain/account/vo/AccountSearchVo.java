package com.monglife.discovery.domain.account.vo;

import lombok.Builder;
import lombok.Getter;

/** 관리자 계정 목록 조회 조건. null 인 조건은 무시한다 */
@Getter
public class AccountSearchVo {

    /** 이메일 / 이름 / 소셜 ID like */
    private final String query;

    private final String platform;

    private final String role;

    private final Boolean isDeleted;

    private final int page;

    private final int size;

    /** accountId | createdAt */
    private final String sortKey;

    private final boolean sortDesc;

    @Builder
    public AccountSearchVo(String query, String platform, String role, Boolean isDeleted, int page, int size, String sortKey, boolean sortDesc) {
        this.query = query;
        this.platform = platform;
        this.role = role;
        this.isDeleted = isDeleted;
        this.page = page;
        this.size = size;
        this.sortKey = sortKey;
        this.sortDesc = sortDesc;
    }
}
