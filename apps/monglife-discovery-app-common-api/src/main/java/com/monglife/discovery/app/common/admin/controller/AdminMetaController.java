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
@RequestMapping("/admin/meta")
public class AdminMetaController {

    private final AdminMetaService adminMetaService;

    @EntryLoggingPoint
    @GetMapping("/filters")
    public ResponseEntity<ResponseDto<AdminFilterOptionsResponseDto>> filters() {
        return ResponseEntity.ok().body(AdminResponse.DISCOVERY_APP_ADMIN_META_FILTERS.toResponseDto(adminMetaService.getFilterOptions()));
    }
}
