package com.monglife.discovery.domain.account.repositoryImpl;

import com.monglife.discovery.domain.account.entity.AccountEntity;
import com.monglife.discovery.domain.account.repository.AccountCustomRepository;
import com.monglife.discovery.domain.account.vo.AccountSearchVo;
import com.monglife.discovery.domain.account.vo.DateCountVo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.DateExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.monglife.discovery.domain.account.entity.QAccountEntity.accountEntity;

@Repository
public class AccountCustomRepositoryImpl implements AccountCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public AccountCustomRepositoryImpl(@Qualifier("accountJpaQueryFactory") JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Optional<AccountEntity> findByEmail(String email) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(accountEntity)
                .where(accountEntity.email.eq(email), accountEntity.isDeleted.eq(false))
                .fetchOne());
    }

    @Override
    public Optional<AccountEntity> findBySocialAccountId(String socialAccountId) {
        return Optional.ofNullable(jpaQueryFactory.selectFrom(accountEntity)
                .where(accountEntity.socialAccountId.eq(socialAccountId), accountEntity.isDeleted.eq(false))
                .fetchOne());
    }

    @Override
    public Optional<AccountEntity> findByAccountId(Long accountId) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(accountEntity)
                .where(accountEntity.accountId.eq(accountId), accountEntity.isDeleted.eq(false))
                .fetchOne());
    }

    // ----- 관리자 -----

    @Override
    public Optional<AccountEntity> findByAccountIdIncludingDeleted(Long accountId) {
        return Optional.ofNullable(jpaQueryFactory
                .selectFrom(accountEntity)
                .where(accountEntity.accountId.eq(accountId))
                .fetchOne());
    }

    private BooleanBuilder searchCondition(AccountSearchVo cond) {
        BooleanBuilder where = new BooleanBuilder();
        if (cond.getQuery() != null && !cond.getQuery().isBlank()) {
            String q = cond.getQuery().trim();
            where.and(accountEntity.email.containsIgnoreCase(q)
                    .or(accountEntity.name.containsIgnoreCase(q))
                    .or(accountEntity.socialAccountId.containsIgnoreCase(q)));
        }
        if (cond.getPlatform() != null) where.and(accountEntity.platform.eq(cond.getPlatform()));
        if (cond.getRole() != null) where.and(accountEntity.role.eq(cond.getRole()));
        if (cond.getIsDeleted() != null) where.and(accountEntity.isDeleted.eq(cond.getIsDeleted()));
        return where;
    }

    private OrderSpecifier<?> order(AccountSearchVo cond) {
        boolean desc = cond.isSortDesc();
        if ("createdAt".equals(cond.getSortKey())) {
            return desc ? accountEntity.createdAt.desc() : accountEntity.createdAt.asc();
        }
        return desc ? accountEntity.accountId.desc() : accountEntity.accountId.asc();
    }

    @Override
    public List<AccountEntity> findPage(AccountSearchVo cond) {
        return jpaQueryFactory.selectFrom(accountEntity)
                .where(searchCondition(cond))
                .orderBy(order(cond), accountEntity.accountId.desc())
                .offset((long) cond.getPage() * cond.getSize())
                .limit(cond.getSize())
                .fetch();
    }

    @Override
    public long countPage(AccountSearchVo cond) {
        Long count = jpaQueryFactory.select(accountEntity.count())
                .from(accountEntity)
                .where(searchCondition(cond))
                .fetchOne();
        return count == null ? 0 : count;
    }

    @Override
    public List<AccountEntity> findAllByAccountIds(Collection<Long> accountIds) {
        if (accountIds.isEmpty()) return List.of();
        return jpaQueryFactory.selectFrom(accountEntity)
                .where(accountEntity.accountId.in(accountIds))
                .fetch();
    }

    @Override
    public List<Long> findAccountIdsByQuery(String query) {
        return jpaQueryFactory.select(accountEntity.accountId)
                .from(accountEntity)
                .where(accountEntity.email.containsIgnoreCase(query)
                        .or(accountEntity.name.containsIgnoreCase(query)))
                .fetch();
    }

    @Override
    public long countJoinedSince(LocalDateTime since) {
        Long count = jpaQueryFactory.select(accountEntity.count())
                .from(accountEntity)
                .where(accountEntity.createdAt.goe(since))
                .fetchOne();
        return count == null ? 0 : count;
    }

    @Override
    public long countActive() {
        Long count = jpaQueryFactory.select(accountEntity.count())
                .from(accountEntity)
                .where(accountEntity.isDeleted.eq(false))
                .fetchOne();
        return count == null ? 0 : count;
    }

    @Override
    public List<DateCountVo> countJoinedGroupByDate(LocalDateTime from, LocalDateTime to) {
        // JPQL 표준 함수만 쓴다 (H2 / MySQL 공통). date(created_at) 로 그룹.
        DateExpression<LocalDate> date = Expressions.dateTemplate(LocalDate.class, "cast({0} as date)", accountEntity.createdAt);
        List<Tuple> rows = jpaQueryFactory.select(date, accountEntity.count())
                .from(accountEntity)
                .where(accountEntity.createdAt.goe(from), accountEntity.createdAt.lt(to))
                .groupBy(date)
                .orderBy(date.asc())
                .fetch();
        return rows.stream()
                .map(t -> DateCountVo.builder()
                        .date(toLocalDate(t.get(0, Object.class)))
                        .count(t.get(1, Number.class) == null ? 0 : t.get(1, Number.class).longValue())
                        .uniqueCount(0)
                        .build())
                .toList();
    }

    /** 드라이버에 따라 java.sql.Date / LocalDate 로 온다 */
    static LocalDate toLocalDate(Object value) {
        if (value instanceof LocalDate d) return d;
        if (value instanceof java.sql.Date d) return d.toLocalDate();
        if (value instanceof java.util.Date d) return new java.sql.Date(d.getTime()).toLocalDate();
        return LocalDate.parse(String.valueOf(value));
    }
}
