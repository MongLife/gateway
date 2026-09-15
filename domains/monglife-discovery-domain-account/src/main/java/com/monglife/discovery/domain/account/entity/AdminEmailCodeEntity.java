package com.monglife.discovery.domain.account.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.time.LocalDateTime;

/** 관리자 이메일 인증 코드. TTL 이 지나면 Redis 가 지운다 */
@Getter
@NoArgsConstructor
@RedisHash("monglife_admin_email_code")
public class AdminEmailCodeEntity {

    @Id
    private String email;

    private String code;

    private LocalDateTime issuedAt;

    @TimeToLive
    private Long expiration;

    @Builder
    public AdminEmailCodeEntity(String email, String code, LocalDateTime issuedAt, Long expiration) {
        this.email = email;
        this.code = code;
        this.issuedAt = issuedAt;
        this.expiration = expiration;
    }
}
