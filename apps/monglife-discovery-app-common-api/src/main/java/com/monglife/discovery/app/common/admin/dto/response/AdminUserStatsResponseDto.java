package com.monglife.discovery.app.common.admin.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AdminUserStatsResponseDto {

    private final long todayJoined;

    private final long weekJoined;

    private final long totalAccounts;

    private final long inactiveAccounts;

    private final long activeSessions;

    @Builder
    public AdminUserStatsResponseDto(long todayJoined, long weekJoined, long totalAccounts, long inactiveAccounts, long activeSessions) {
        this.todayJoined = todayJoined;
        this.weekJoined = weekJoined;
        this.totalAccounts = totalAccounts;
        this.inactiveAccounts = inactiveAccounts;
        this.activeSessions = activeSessions;
    }
}
