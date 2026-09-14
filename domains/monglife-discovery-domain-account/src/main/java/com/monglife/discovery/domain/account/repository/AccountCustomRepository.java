package com.monglife.discovery.domain.account.repository;

import com.monglife.discovery.domain.account.entity.AccountEntity;
import com.monglife.discovery.domain.account.vo.AccountSearchVo;
import com.monglife.discovery.domain.account.vo.DateCountVo;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AccountCustomRepository {

    Optional<AccountEntity> findByEmail(String email);

    Optional<AccountEntity> findBySocialAccountId(String socialAccountId);

    Optional<AccountEntity> findByAccountId(Long accountId);

    // ----- 관리자 -----

    /** 탈퇴 계정도 포함해 조회한다 (관리자 상세용) */
    Optional<AccountEntity> findByAccountIdIncludingDeleted(Long accountId);

    List<AccountEntity> findPage(AccountSearchVo cond);

    long countPage(AccountSearchVo cond);

    List<AccountEntity> findAllByAccountIds(Collection<Long> accountIds);

    /** 이메일/이름이 needle 을 포함하는 계정 ID (오류 신고 검색용) */
    List<Long> findAccountIdsByQuery(String query);

    long countJoinedSince(LocalDateTime since);

    long countActive();

    List<DateCountVo> countJoinedGroupByDate(LocalDateTime from, LocalDateTime to);
}
