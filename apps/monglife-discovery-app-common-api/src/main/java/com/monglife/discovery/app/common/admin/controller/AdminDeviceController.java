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

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/devices")
public class AdminDeviceController {

    private final AdminDeviceService adminDeviceService;

    @EntryLoggingPoint
    @GetMapping
    public ResponseEntity<PageResponseDto<List<AdminDeviceResponseDto>>> list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Boolean unmappedOnly,
            @RequestParam(required = false) String fcm,
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) String sort
    ) {
        SortSpec sortSpec = SortSpec.parse(sort, java.util.Set.of("createdAt"), "createdAt", true);
        return adminDeviceService.getDevices(
                PageQuery.blankToNull(query), unmappedOnly, PageQuery.blankToNull(fcm), PageQuery.blankToNull(deviceName),
                sortSpec.desc(), PageQuery.page(page), PageQuery.size(size)
        ).toResponse(AdminResponse.DISCOVERY_APP_ADMIN_DEVICE_LIST);
    }

    @EntryLoggingPoint
    @PutMapping("/{deviceId}/account")
    public ResponseEntity<ResponseDto<AdminDeviceResponseDto>> connect(@PathVariable String deviceId, @Valid @RequestBody AdminDeviceConnectRequestDto dto) {
        return ResponseEntity.ok().body(AdminResponse.DISCOVERY_APP_ADMIN_DEVICE_CONNECT.toResponseDto(adminDeviceService.connect(deviceId, dto.getAccountId())));
    }

    @EntryLoggingPoint
    @DeleteMapping("/{deviceId}/account")
    public ResponseEntity<ResponseDto<AdminDeviceResponseDto>> disconnect(@PathVariable String deviceId) {
        return ResponseEntity.ok().body(AdminResponse.DISCOVERY_APP_ADMIN_DEVICE_DISCONNECT.toResponseDto(adminDeviceService.disconnect(deviceId)));
    }

    @EntryLoggingPoint
    @DeleteMapping("/{deviceId}")
    public ResponseEntity<ResponseDto<?>> delete(@PathVariable String deviceId) {
        adminDeviceService.delete(deviceId);
        return ResponseEntity.ok().body(AdminResponse.DISCOVERY_APP_ADMIN_DEVICE_DELETE.toResponseDto());
    }
}
