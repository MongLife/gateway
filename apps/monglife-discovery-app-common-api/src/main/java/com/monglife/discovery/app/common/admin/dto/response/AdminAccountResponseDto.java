package com.monglife.discovery.app.common.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AdminAccountResponseDto {

    private final Long accountId;

    private final String socialAccountId;

    private final String platform;

    private final String email;

    private final String name;

    private final String role;

    private final Boolean isDeleted;

    // 기본 ObjectMapper 가 날짜를 배열로 내보내므로 ISO 문자열로 고정한다
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd\'T\'HH:mm:ss")
    private final LocalDateTime createdAt;

    // 기본 ObjectMapper 가 날짜를 배열로 내보내므로 ISO 문자열로 고정한다
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd\'T\'HH:mm:ss")
    private final LocalDateTime updatedAt;

    @Builder
    public AdminAccountResponseDto(Long accountId, String socialAccountId, String platform, String email, String name, String role, Boolean isDeleted, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.accountId = accountId;
        this.socialAccountId = socialAccountId;
        this.platform = platform;
        this.email = email;
        this.name = name;
        this.role = role;
        this.isDeleted = isDeleted;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
