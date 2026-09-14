package com.monglife.discovery.domain.account.vo;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AdminEmailCodeVo {

    private final String email;

    private final String code;

    private final LocalDateTime issuedAt;

    private final Long expiration;

    @Builder
    public AdminEmailCodeVo(String email, String code, LocalDateTime issuedAt, Long expiration) {
        this.email = email;
        this.code = code;
        this.issuedAt = issuedAt;
        this.expiration = expiration;
    }
}
