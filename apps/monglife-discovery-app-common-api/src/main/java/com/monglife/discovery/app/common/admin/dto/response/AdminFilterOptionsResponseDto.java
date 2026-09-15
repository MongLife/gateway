package com.monglife.discovery.app.common.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AdminFilterOptionsResponseDto {

    private final List<String> deviceNames;

    private final List<String> appPackageNames;

    private final List<String> buildVersions;

    @Builder
    public AdminFilterOptionsResponseDto(List<String> deviceNames, List<String> appPackageNames, List<String> buildVersions) {
        this.deviceNames = deviceNames;
        this.appPackageNames = appPackageNames;
        this.buildVersions = buildVersions;
    }
}
