package com.monglife.discovery.domain.feedback.repositoryImpl;

import com.monglife.discovery.domain.feedback.entity.FeedbackEntity;
import com.monglife.discovery.domain.feedback.repository.FeedbackCustomRepository;
import com.monglife.discovery.domain.feedback.vo.FeedbackSearchVo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.monglife.discovery.domain.feedback.entity.QFeedbackEntity.feedbackEntity;

@Repository
public class FeedbackCustomRepositoryImpl implements FeedbackCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public FeedbackCustomRepositoryImpl(@Qualifier("feedbackJpaQueryFactory") JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    private BooleanBuilder searchCondition(FeedbackSearchVo cond) {
        BooleanBuilder where = new BooleanBuilder();
        if (cond.getQuery() != null && !cond.getQuery().isBlank()) {
            BooleanExpression byText = feedbackEntity.title.containsIgnoreCase(cond.getQuery().trim());
            if (cond.getQueryAccountIds() != null && !cond.getQueryAccountIds().isEmpty()) {
                byText = byText.or(feedbackEntity.accountId.in(cond.getQueryAccountIds()));
            }
            where.and(byText);
        }
        if (cond.getStatus() != null) where.and(feedbackEntity.status.eq(cond.getStatus()));
        if (cond.getDeviceName() != null) where.and(feedbackEntity.deviceName.eq(cond.getDeviceName()));
        if (cond.getAppPackageName() != null) where.and(feedbackEntity.appPackageName.eq(cond.getAppPackageName()));
        if (cond.getBuildVersion() != null) where.and(feedbackEntity.buildVersion.eq(cond.getBuildVersion()));
        return where;
    }

    private OrderSpecifier<?> order(FeedbackSearchVo cond) {
        boolean desc = cond.isSortDesc();
        if ("feedbackId".equals(cond.getSortKey())) {
            return desc ? feedbackEntity.feedbackId.desc() : feedbackEntity.feedbackId.asc();
        }
        return desc ? feedbackEntity.createdAt.desc() : feedbackEntity.createdAt.asc();
    }

    @Override
    public List<FeedbackEntity> findPage(FeedbackSearchVo cond) {
        return jpaQueryFactory.selectFrom(feedbackEntity)
                .where(searchCondition(cond))
                .orderBy(order(cond), feedbackEntity.feedbackId.desc())
                .offset((long) cond.getPage() * cond.getSize())
                .limit(cond.getSize())
                .fetch();
    }

    @Override
    public long countPage(FeedbackSearchVo cond) {
        Long count = jpaQueryFactory.select(feedbackEntity.count())
                .from(feedbackEntity)
                .where(searchCondition(cond))
                .fetchOne();
        return count == null ? 0 : count;
    }
}
