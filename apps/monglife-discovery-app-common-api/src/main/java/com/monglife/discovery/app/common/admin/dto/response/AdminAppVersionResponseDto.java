package com.monglife.discovery.app.common.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AdminAppVersionResponseDto {

    private final Long appVersionId;

    private final String appPackageName;

    private final String buildVersion;

    private final Boolean mustUpdate;

    // 기본 ObjectMapper 가 날짜를 배열로 내보내므로 ISO 문자열로 고정한다
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd\'T\'HH:mm:ss")
    private final LocalDateTime createdAt;

    // 기본 ObjectMapper 가 날짜를 배열로 내보내므로 ISO 문자열로 고정한다
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd\'T\'HH:mm:ss")
    private final LocalDateTime updatedAt;

    @Builder
    public AdminAppVersionResponseDto(Long appVersionId, String appPackageName, String buildVersion, Boolean mustUpdate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.appVersionId = appVersionId;
        this.appPackageName = appPackageName;
        this.buildVersion = buildVersion;
        this.mustUpdate = mustUpdate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
