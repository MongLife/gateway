package com.monglife.discovery.app.common.admin.service;

import com.monglife.discovery.app.common.admin.dto.response.AdminLoginStatResponseDto;
import com.monglife.discovery.app.common.admin.dto.response.AdminSignupStatResponseDto;
import com.monglife.discovery.app.common.admin.dto.response.AdminUserStatsResponseDto;
import com.monglife.discovery.domain.account.service.AccountService;
import com.monglife.discovery.domain.account.service.LoginHistoryService;
import com.monglife.discovery.domain.account.service.TokenService;
import com.monglife.discovery.domain.account.vo.DateCountVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class AdminStatsService {

    public static final int INACTIVE_DAYS = 30;
    public static final int MAX_DAYS = 90;

    private final AccountService accountService;
    private final LoginHistoryService loginHistoryService;
    private final TokenService tokenService;

    @Transactional(readOnly = true)
    public AdminUserStatsResponseDto getUserStats() {
        LocalDate today = LocalDate.now();
        long total = accountService.countActive();
        // 30일 미로그인 = 활성 계정 - 최근 30일 로그인 계정 (탈퇴 계정은 로그인 이력이 있어도 제외되도록 교집합은 안 쓴다)
        HashSet<Long> recent = new HashSet<>(loginHistoryService.getAccountIdsLoggedInSince(today.minusDays(INACTIVE_DAYS)));
        long inactive = Math.max(0, total - recent.size());
        return AdminUserStatsResponseDto.builder()
                .todayJoined(accountService.countJoinedSince(today.atStartOfDay()))
                .weekJoined(accountService.countJoinedSince(today.minusDays(6).atStartOfDay()))
                .totalAccounts(total)
                .inactiveAccounts(inactive)
                .activeSessions(tokenService.getTokens().size())
                .build();
    }

    @Transactional(readOnly = true)
    public List<AdminLoginStatResponseDto> getLoginStats(int days) {
        int d = clamp(days);
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(d - 1L);
        Map<LocalDate, DateCountVo> byDate = loginHistoryService.getLoginStats(from, to).stream()
                .collect(Collectors.toMap(DateCountVo::getDate, Function.identity(), (a, b) -> a));
        return IntStream.range(0, d).mapToObj(from::plusDays)
                .map(date -> {
                    DateCountVo v = byDate.get(date);
                    return AdminLoginStatResponseDto.builder()
                            .date(date)
                            .loginCount(v == null ? 0 : v.getCount())
                            .uniqueAccounts(v == null ? 0 : v.getUniqueCount())
                            .build();
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminSignupStatResponseDto> getSignupStats(int days) {
        int d = clamp(days);
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusDays(d - 1L);
        Map<LocalDate, DateCountVo> byDate = accountService.getSignupStats(from.atStartOfDay(), to.plusDays(1).atStartOfDay()).stream()
                .collect(Collectors.toMap(DateCountVo::getDate, Function.identity(), (a, b) -> a));
        return IntStream.range(0, d).mapToObj(from::plusDays)
                .map(date -> AdminSignupStatResponseDto.builder()
                        .date(date)
                        .count(byDate.containsKey(date) ? byDate.get(date).getCount() : 0)
                        .build())
                .toList();
    }

    private static int clamp(int days) {
        return Math.max(1, Math.min(days, MAX_DAYS));
    }
}
