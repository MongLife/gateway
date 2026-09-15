package com.monglife.discovery.app.common.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AdminLoginHistoryResponseDto {

    private final Long accountLogId;

    private final Long accountId;

    private final String deviceId;

    private final String appPackageName;

    private final String deviceName;

    private final String buildVersion;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate loginAt;

    private final Integer loginCount;

    @Builder
    public AdminLoginHistoryResponseDto(Long accountLogId, Long accountId, String deviceId, String appPackageName, String deviceName, String buildVersion, LocalDate loginAt, Integer loginCount) {
        this.accountLogId = accountLogId;
        this.accountId = accountId;
        this.deviceId = deviceId;
        this.appPackageName = appPackageName;
        this.deviceName = deviceName;
        this.buildVersion = buildVersion;
        this.loginAt = loginAt;
        this.loginCount = loginCount;
    }
}
