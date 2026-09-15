package com.monglife.discovery.app.common.admin.service;

import com.monglife.discovery.app.common.admin.dto.response.AdminFilterOptionsResponseDto;
import com.monglife.discovery.domain.device.service.AppVersionService;
import com.monglife.discovery.domain.device.service.DeviceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdminMetaService {

    private final DeviceService deviceService;
    private final AppVersionService appVersionService;

    /** 필터 셀렉트 옵션 (distinct 값) */
    @Transactional(readOnly = true)
    public AdminFilterOptionsResponseDto getFilterOptions() {
        return AdminFilterOptionsResponseDto.builder()
                .deviceNames(deviceService.getDeviceNames())
                .appPackageNames(appVersionService.getPackageNames())
                .buildVersions(appVersionService.getBuildVersions())
                .build();
    }
}
