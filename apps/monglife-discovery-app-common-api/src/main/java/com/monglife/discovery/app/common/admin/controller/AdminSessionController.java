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
@RequestMapping("/admin/sessions")
public class AdminSessionController {

    private final AdminSessionService adminSessionService;

    /** 로그인 현황 (토큰 + 계정·기기 요약) */
    @EntryLoggingPoint
    @GetMapping
    public ResponseEntity<PageResponseDto<List<AdminSessionResponseDto>>> list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) String appPackageName,
            @RequestParam(required = false) String buildVersion,
            @RequestParam(required = false) String sort
    ) {
        SortSpec sortSpec = SortSpec.parse(sort, java.util.Set.of("createdAt"), "createdAt", true);
        return adminSessionService.getSessions(null,
                PageQuery.blankToNull(deviceName), PageQuery.blankToNull(appPackageName), PageQuery.blankToNull(buildVersion),
                sortSpec.desc(), PageQuery.page(page), PageQuery.size(size)
        ).toResponse(AdminResponse.DISCOVERY_APP_ADMIN_SESSION_LIST);
    }

    /** 계정별 토큰 (계정 상세 탭) */
    @EntryLoggingPoint
    @GetMapping("/tokens")
    public ResponseEntity<PageResponseDto<List<AdminSessionResponseDto>>> tokens(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) Long accountId
    ) {
        return adminSessionService.getSessions(accountId, null, null, null, true, PageQuery.page(page), PageQuery.size(size))
                .toResponse(AdminResponse.DISCOVERY_APP_ADMIN_SESSION_LIST);
    }

    /** 계정의 모든 세션 종료. /{refreshToken} 보다 앞에 둬야 경로가 겹치지 않는다 */
    @EntryLoggingPoint
    @DeleteMapping("/accounts/{accountId}")
    public ResponseEntity<ResponseDto<?>> revokeAll(@PathVariable Long accountId) {
        adminSessionService.revokeAll(accountId);
        return ResponseEntity.ok().body(AdminResponse.DISCOVERY_APP_ADMIN_SESSION_DELETE.toResponseDto());
    }

    /** 토큰 만료 = 로그아웃 처리 */
    @DeleteMapping("/{refreshToken}")
    public ResponseEntity<ResponseDto<?>> revoke(@PathVariable String refreshToken) {
        adminSessionService.revoke(refreshToken);
        return ResponseEntity.ok().body(AdminResponse.DISCOVERY_APP_ADMIN_SESSION_DELETE.toResponseDto());
    }
}
