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
@RequestMapping("/admin/stats")
public class AdminStatsController {

    private final AdminStatsService adminStatsService;

    @EntryLoggingPoint
    @GetMapping("/users")
    public ResponseEntity<ResponseDto<AdminUserStatsResponseDto>> users() {
        return ResponseEntity.ok().body(AdminResponse.DISCOVERY_APP_ADMIN_STATS.toResponseDto(adminStatsService.getUserStats()));
    }

    @EntryLoggingPoint
    @GetMapping("/logins")
    public ResponseEntity<ResponseDto<List<AdminLoginStatResponseDto>>> logins(@RequestParam(required = false, defaultValue = "14") int days) {
        return ResponseEntity.ok().body(AdminResponse.DISCOVERY_APP_ADMIN_STATS.toResponseDto(adminStatsService.getLoginStats(days)));
    }

    @EntryLoggingPoint
    @GetMapping("/signups")
    public ResponseEntity<ResponseDto<List<AdminSignupStatResponseDto>>> signups(@RequestParam(required = false, defaultValue = "14") int days) {
        return ResponseEntity.ok().body(AdminResponse.DISCOVERY_APP_ADMIN_STATS.toResponseDto(adminStatsService.getSignupStats(days)));
    }
}
