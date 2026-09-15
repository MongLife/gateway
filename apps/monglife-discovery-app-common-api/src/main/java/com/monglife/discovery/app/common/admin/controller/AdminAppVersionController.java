package com.monglife.discovery.app.common.admin.controller;

import com.monglife.core.dto.response.PageResponseDto;
import com.monglife.core.dto.response.ResponseDto;
import com.monglife.discovery.app.common.admin.dto.request.*;
import com.monglife.discovery.app.common.admin.dto.response.*;
import com.monglife.discovery.app.common.admin.enums.AdminResponse;
import com.monglife.discovery.app.common.admin.service.*;
import com.monglife.discovery.app.common.admin.util.PageQuery;
import com.monglife.discovery.app.common.admin.util.SortSpec;
import com.monglife.module.common.logging.annotation.EntryLoggingPoint;
import com.monglife.module.common.security.principal.Passport;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.monglife.discovery.domain.device.service.AppVersionService;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/app-versions")
public class AdminAppVersionController {

    private final AppVersionService appVersionService;

    @EntryLoggingPoint
    @GetMapping
    public ResponseEntity<ResponseDto<List<AdminAppVersionResponseDto>>> list() {
        List<AdminAppVersionResponseDto> items = appVersionService.getAppVersions().stream().map(AdminAppVersionController::toDto).toList();
        return ResponseEntity.ok().body(AdminResponse.DISCOVERY_APP_ADMIN_APP_VERSION_LIST.toResponseDto(items));
    }

    @EntryLoggingPoint
    @PostMapping
    public ResponseEntity<ResponseDto<AdminAppVersionResponseDto>> create(@Valid @RequestBody AdminAppVersionCreateRequestDto dto) {
        return ResponseEntity.ok().body(AdminResponse.DISCOVERY_APP_ADMIN_APP_VERSION_CREATE.toResponseDto(
                toDto(appVersionService.createAppVersion(dto.getAppPackageName().trim(), dto.getBuildVersion().trim(), dto.getMustUpdate()))));
    }

    @EntryLoggingPoint
    @PatchMapping("/{appVersionId}")
    public ResponseEntity<ResponseDto<AdminAppVersionResponseDto>> patch(@PathVariable Long appVersionId, @Valid @RequestBody AdminAppVersionPatchRequestDto dto) {
        return ResponseEntity.ok().body(AdminResponse.DISCOVERY_APP_ADMIN_APP_VERSION_UPDATE.toResponseDto(
                toDto(appVersionService.updateMustUpdate(appVersionId, dto.getMustUpdate()))));
    }

    @EntryLoggingPoint
    @DeleteMapping("/{appVersionId}")
    public ResponseEntity<ResponseDto<?>> delete(@PathVariable Long appVersionId) {
        appVersionService.deleteAppVersion(appVersionId);
        return ResponseEntity.ok().body(AdminResponse.DISCOVERY_APP_ADMIN_APP_VERSION_DELETE.toResponseDto());
    }

    private static AdminAppVersionResponseDto toDto(com.monglife.discovery.domain.device.vo.AppVersionVo v) {
        return AdminAppVersionResponseDto.builder()
                .appVersionId(v.getAppVersionId())
                .appPackageName(v.getAppPackageName())
                .buildVersion(v.getBuildVersion())
                .mustUpdate(v.getMustUpdate())
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .build();
    }
}
