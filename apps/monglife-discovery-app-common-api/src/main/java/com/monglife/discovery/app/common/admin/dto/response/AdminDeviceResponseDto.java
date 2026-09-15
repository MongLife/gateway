package com.monglife.discovery.app.common.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AdminDeviceResponseDto {

    private final String deviceId;

    private final String deviceName;

    private final String fcmToken;

    private final Long accountId;

    private final String accountEmail;

    private final String accountName;

    // 기본 ObjectMapper 가 날짜를 배열로 내보내므로 ISO 문자열로 고정한다
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd\'T\'HH:mm:ss")
    private final LocalDateTime createdAt;

    // 기본 ObjectMapper 가 날짜를 배열로 내보내므로 ISO 문자열로 고정한다
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd\'T\'HH:mm:ss")
    private final LocalDateTime updatedAt;

    @Builder
    public AdminDeviceResponseDto(String deviceId, String deviceName, String fcmToken, Long accountId, String accountEmail, String accountName, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.fcmToken = fcmToken;
        this.accountId = accountId;
        this.accountEmail = accountEmail;
        this.accountName = accountName;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
