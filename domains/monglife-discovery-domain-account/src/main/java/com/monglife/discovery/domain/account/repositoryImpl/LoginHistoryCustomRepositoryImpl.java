package com.monglife.discovery.domain.account.repositoryImpl;

import com.monglife.discovery.domain.account.entity.LoginHistoryEntity;
import com.monglife.discovery.domain.account.repository.LoginHistoryCustomRepository;
import com.monglife.discovery.domain.account.vo.DateCountVo;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.monglife.discovery.domain.account.entity.QLoginHistoryEntity.loginHistoryEntity;

@Repository
public class LoginHistoryCustomRepositoryImpl implements LoginHistoryCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public LoginHistoryCustomRepositoryImpl(@Qualifier("accountJpaQueryFactory") JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Optional<LoginHistoryEntity> findByAccountIdAndDeviceIdAndLoginAt(Long accountId, String deviceId, LocalDate loginAt) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(loginHistoryEntity)
                .where(loginHistoryEntity.accountId.eq(accountId), loginHistoryEntity.deviceId.eq(deviceId), loginHistoryEntity.loginAt.eq(loginAt))
                .fetchOne());
    }

    // ----- 관리자 -----

    @Override
    public List<LoginHistoryEntity> findByAccountId(Long accountId) {
        return jpaQueryFactory.selectFrom(loginHistoryEntity)
                .where(loginHistoryEntity.accountId.eq(accountId))
                .orderBy(loginHistoryEntity.loginAt.desc(), loginHistoryEntity.accountLogId.desc())
                .fetch();
    }

    @Override
    public List<Long> findAccountIdsLoggedInSince(LocalDate since) {
        return jpaQueryFactory.select(loginHistoryEntity.accountId).distinct()
                .from(loginHistoryEntity)
                .where(loginHistoryEntity.loginAt.goe(since))
                .fetch();
    }

    @Override
    public List<DateCountVo> countLoginGroupByDate(LocalDate from, LocalDate to) {
        List<Tuple> rows = jpaQueryFactory
                .select(loginHistoryEntity.loginAt, loginHistoryEntity.loginCount.sum(), loginHistoryEntity.accountId.countDistinct())
                .from(loginHistoryEntity)
                .where(loginHistoryEntity.loginAt.goe(from), loginHistoryEntity.loginAt.loe(to))
                .groupBy(loginHistoryEntity.loginAt)
                .orderBy(loginHistoryEntity.loginAt.asc())
                .fetch();
        return rows.stream()
                .map(t -> DateCountVo.builder()
                        .date(t.get(loginHistoryEntity.loginAt))
                        // SUM 은 드라이버에 따라 Integer/Long/BigDecimal 로 온다
                        .count(t.get(1, Number.class) == null ? 0 : t.get(1, Number.class).longValue())
                        .uniqueCount(t.get(2, Number.class) == null ? 0 : t.get(2, Number.class).longValue())
                        .build())
                .toList();
    }
}
