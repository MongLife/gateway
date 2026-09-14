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

/** 화면 이름은 "오류 신고" 라 경로가 error-reports 다 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/error-reports")
public class AdminFeedbackController {

    private final AdminFeedbackService adminFeedbackService;

    @EntryLoggingPoint
    @GetMapping
    public ResponseEntity<PageResponseDto<List<AdminFeedbackResponseDto>>> list(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String deviceName,
            @RequestParam(required = false) String appPackageName,
            @RequestParam(required = false) String buildVersion,
            @RequestParam(required = false) String sort
    ) {
        SortSpec sortSpec = SortSpec.parse(sort, AdminFeedbackService.SORT_KEYS, "createdAt", true);
        return adminFeedbackService.getFeedbacks(
                PageQuery.blankToNull(query), PageQuery.blankToNull(status), PageQuery.blankToNull(deviceName),
                PageQuery.blankToNull(appPackageName), PageQuery.blankToNull(buildVersion),
                PageQuery.page(page), PageQuery.size(size), sortSpec
        ).toResponse(AdminResponse.DISCOVERY_APP_ADMIN_FEEDBACK_LIST);
    }

    @EntryLoggingPoint
    @GetMapping("/{reportId}")
    public ResponseEntity<ResponseDto<AdminFeedbackResponseDto>> get(@PathVariable Long reportId) {
        return ResponseEntity.ok().body(AdminResponse.DISCOVERY_APP_ADMIN_FEEDBACK_GET.toResponseDto(adminFeedbackService.getFeedback(reportId)));
    }

    /** 답변 — 신고자 이메일로 발송. @EntryLoggingPoint 없음 (본문이 로그에 남는다) */
    @PostMapping("/{reportId}/replies")
    public ResponseEntity<ResponseDto<AdminFeedbackReplyResponseDto>> reply(
            @AuthenticationPrincipal Passport passport,
            @PathVariable Long reportId,
            @Valid @RequestBody AdminFeedbackReplyRequestDto dto
    ) {
        AdminFeedbackReplyResponseDto reply = adminFeedbackService.reply(reportId, dto.getContent(), passport.getAccountId());
        return ResponseEntity.ok().body(AdminResponse.DISCOVERY_APP_ADMIN_FEEDBACK_REPLY.toResponseDto(reply));
    }
}
