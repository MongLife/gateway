package com.monglife.discovery.domain.device.vo;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AppVersionVo {

    private final Long appVersionId;

    private final String appPackageName;

    private final String buildVersion;

    private final Boolean mustUpdate;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    @Builder
    public AppVersionVo(Long appVersionId, String appPackageName, String buildVersion, Boolean mustUpdate, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.appVersionId = appVersionId;
        this.appPackageName = appPackageName;
        this.buildVersion = buildVersion;
        this.mustUpdate = mustUpdate;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
