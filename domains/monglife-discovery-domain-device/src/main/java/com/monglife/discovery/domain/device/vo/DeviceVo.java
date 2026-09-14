package com.monglife.discovery.domain.device.vo;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DeviceVo {

    private final String deviceId;

    private final String deviceName;

    private final String fcmToken;

    private final Long accountId;

    private final LocalDateTime createdAt;

    private final LocalDateTime updatedAt;

    @Builder
    public DeviceVo(String deviceId, String deviceName, String fcmToken, Long accountId, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.fcmToken = fcmToken;
        this.accountId = accountId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
}
