package com.monglife.discovery.domain.account.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@ToString
@Getter
public class AccountVo {

    private final Long accountId;

    private final String email;

    private final String name;

    private final String socialAccountId;

    private final String role;

    private final String platform;

    private final Boolean isDeleted;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    @Builder
    public AccountVo(Long accountId, String email, String name, String socialAccountId, String role, String platform, Boolean isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.accountId = accountId;
        this.email = email;
        this.name = name;
        this.socialAccountId = socialAccountId;
        this.role = role;
        this.platform = platform;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
