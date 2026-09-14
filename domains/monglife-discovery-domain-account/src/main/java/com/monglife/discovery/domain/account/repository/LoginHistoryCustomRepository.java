package com.monglife.discovery.domain.account.repository;

import com.monglife.discovery.domain.account.entity.LoginHistoryEntity;
import com.monglife.discovery.domain.account.vo.DateCountVo;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LoginHistoryCustomRepository {

    Optional<LoginHistoryEntity> findByAccountIdAndDeviceIdAndLoginAt(Long accountId, String deviceId, LocalDate loginAt);

    // ----- 관리자 -----

    List<LoginHistoryEntity> findByAccountId(Long accountId);

    /** since 이후 로그인 기록이 있는 계정 ID (중복 없음) */
    List<Long> findAccountIdsLoggedInSince(LocalDate since);

    /** 일자별 로그인 횟수 합 · 계정 수 */
    List<DateCountVo> countLoginGroupByDate(LocalDate from, LocalDate to);
}
