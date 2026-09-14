package com.monglife.discovery.domain.account.service;

import com.monglife.discovery.domain.account.entity.LoginHistoryEntity;
import com.monglife.discovery.domain.account.repository.LoginHistoryRepository;
import com.monglife.discovery.domain.account.vo.LoginHistoryVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.monglife.discovery.domain.account.vo.DateCountVo;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginHistoryService {

    private final LoginHistoryRepository loginHistoryRepository;

    /**
     * 로그인 기록 갱신
     */
    @Transactional
    public void patchLoginHistory(LoginHistoryVo loginHistoryVo) {

        LoginHistoryEntity loginHistoryEntity = loginHistoryRepository.findByAccountIdAndDeviceIdAndLoginAt(loginHistoryVo.getAccountId(), loginHistoryVo.getDeviceId(), LocalDate.now())
                .orElseGet(() -> loginHistoryRepository.save(LoginHistoryEntity.builder()
                        .accountId(loginHistoryVo.getAccountId())
                        .deviceId(loginHistoryVo.getDeviceId())
                        .appPackageName(loginHistoryVo.getAppPackageName())
                        .deviceName(loginHistoryVo.getDeviceName())
                        .buildVersion(loginHistoryVo.getBuildVersion())
                        .build()));

        loginHistoryEntity.increaseLoginCount();
    }

    // ----- 관리자 -----

    @Transactional(readOnly = true)
    public List<LoginHistoryVo> getLoginHistories(Long accountId) {
        return loginHistoryRepository.findByAccountId(accountId).stream()
                .map(e -> LoginHistoryVo.builder()
                        .accountLogId(e.getAccountLogId())
                        .accountId(e.getAccountId())
                        .deviceId(e.getDeviceId())
                        .appPackageName(e.getAppPackageName())
                        .deviceName(e.getDeviceName())
                        .buildVersion(e.getBuildVersion())
                        .loginAt(e.getLoginAt())
                        .loginCount(e.getLoginCount())
                        .build())
                .toList();
    }

    /** since 이후 로그인한 계정 ID 목록 */
    @Transactional(readOnly = true)
    public List<Long> getAccountIdsLoggedInSince(LocalDate since) {
        return loginHistoryRepository.findAccountIdsLoggedInSince(since);
    }

    /** 일자별 로그인 집계 (count = 로그인 횟수 합, uniqueCount = 계정 수) */
    @Transactional(readOnly = true)
    public List<DateCountVo> getLoginStats(LocalDate from, LocalDate to) {
        return loginHistoryRepository.countLoginGroupByDate(from, to);
    }
}
