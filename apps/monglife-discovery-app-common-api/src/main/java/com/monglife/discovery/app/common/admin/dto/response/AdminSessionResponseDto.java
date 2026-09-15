package com.monglife.discovery.app.common.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AdminSessionResponseDto {

    private final String refreshToken;

    private final String accessToken;

    private final String deviceId;

    private final Long accountId;

    private final String appPackageName;

    private final String buildVersion;

    // 기본 ObjectMapper 가 날짜를 배열로 내보내므로 ISO 문자열로 고정한다
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd\'T\'HH:mm:ss")
    private final LocalDateTime createdAt;

    private final Long expiration;

    private final String email;

    private final String name;

    private final String deviceName;

    @Builder
    public AdminSessionResponseDto(String refreshToken, String accessToken, String deviceId, Long accountId, String appPackageName, String buildVersion, LocalDateTime createdAt, Long expiration, String email, String name, String deviceName) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
        this.deviceId = deviceId;
        this.accountId = accountId;
        this.appPackageName = appPackageName;
        this.buildVersion = buildVersion;
        this.createdAt = createdAt;
        this.expiration = expiration;
        this.email = email;
        this.name = name;
        this.deviceName = deviceName;
    }
}
