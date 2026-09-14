package com.monglife.discovery.domain.account.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@ToString
@Getter
public class LoginHistoryVo {

    private final Long accountLogId;

    private final Long accountId;

    private final String deviceId;

    private final String appPackageName;

    private final String deviceName;

    private final String buildVersion;

    private final LocalDate loginAt;

    private final Integer loginCount;

    @Builder
    public LoginHistoryVo(Long accountLogId, Long accountId, String deviceId, String appPackageName, String deviceName, String buildVersion, LocalDate loginAt, Integer loginCount) {
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
