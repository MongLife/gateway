package com.monglife.discovery.app.common.notification.controller;

import com.monglife.core.dto.event.SendNotificationDto;
import com.monglife.core.dto.response.PageResponseDto;
import com.monglife.core.dto.response.ResponseDto;
import com.monglife.discovery.app.common.admin.dto.response.AdminDeviceResponseDto;
import com.monglife.discovery.app.common.admin.enums.AdminResponse;
import com.monglife.discovery.app.common.admin.service.AdminDeviceService;
import com.monglife.discovery.app.common.admin.util.PageQuery;
import com.monglife.module.common.logging.annotation.EntryLoggingPoint;
import com.monglife.discovery.app.common.notification.dto.request.NotificationRequestDto;
import com.monglife.discovery.app.common.notification.enums.NotificationResponse;
import com.monglife.discovery.app.common.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/notification")
public class NotificationController {

    private final NotificationService notificationService;

    private final AdminDeviceService adminDeviceService;

    /**
     * Mongs 알림 전송
     * @param notificationRequestDto 알림 전송 Dto
     * @return 성공 여부
     */
    @PostMapping("/mongs")
    public ResponseEntity<ResponseDto<?>> notification(@Valid @RequestBody NotificationRequestDto notificationRequestDto) {

        SendNotificationDto sendNotificationDto = SendNotificationDto.builder()
                .accountId(notificationRequestDto.getAccountId())
                .title(notificationRequestDto.getTitle())
                .body(notificationRequestDto.getBody())
                .build();

        notificationService.sendNotification(sendNotificationDto);

        return ResponseEntity.ok().body(NotificationResponse.DISCOVERY_APP_NOTIFICATION.toResponseDto());
    }

    /**
     * 알림 가능 기기 목록 (FCM 토큰 + 계정 연결). 관리자 웹의 전송 대상 선택용
     */
    @EntryLoggingPoint
    @GetMapping("/devices")
    public ResponseEntity<PageResponseDto<List<AdminDeviceResponseDto>>> devices(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) Long accountId,
            @RequestParam(required = false) String deviceName
    ) {
        return adminDeviceService.getNotifiableDevices(PageQuery.blankToNull(query), accountId, PageQuery.blankToNull(deviceName), PageQuery.page(page), PageQuery.size(size))
                .toResponse(AdminResponse.DISCOVERY_APP_ADMIN_NOTIFICATION_DEVICE_LIST);
    }
}
