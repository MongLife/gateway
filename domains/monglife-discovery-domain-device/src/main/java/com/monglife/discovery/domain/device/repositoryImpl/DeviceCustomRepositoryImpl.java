package com.monglife.discovery.domain.device.repositoryImpl;

import com.monglife.discovery.domain.device.entity.DeviceEntity;
import com.monglife.discovery.domain.device.repository.DeviceCustomRepository;
import com.monglife.discovery.domain.device.vo.DeviceSearchVo;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.monglife.discovery.domain.device.entity.QDeviceEntity.deviceEntity;

@Repository
public class DeviceCustomRepositoryImpl implements DeviceCustomRepository {

    private final JPAQueryFactory jpaQueryFactory;

    public DeviceCustomRepositoryImpl(@Qualifier("deviceJpaQueryFactory") JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    private BooleanBuilder searchCondition(DeviceSearchVo cond) {
        BooleanBuilder where = new BooleanBuilder();
        if (cond.getQuery() != null && !cond.getQuery().isBlank()) {
            String q = cond.getQuery().trim();
            BooleanExpression byText = deviceEntity.deviceId.containsIgnoreCase(q)
                    .or(deviceEntity.deviceName.containsIgnoreCase(q));
            if (cond.getQueryAccountIds() != null && !cond.getQueryAccountIds().isEmpty()) {
                byText = byText.or(deviceEntity.accountId.in(cond.getQueryAccountIds()));
            }
            where.and(byText);
        }
        if (Boolean.TRUE.equals(cond.getUnmappedOnly())) where.and(deviceEntity.accountId.isNull());
        if (cond.getHasFcmToken() != null) {
            where.and(cond.getHasFcmToken() ? deviceEntity.fcmToken.isNotNull() : deviceEntity.fcmToken.isNull());
        }
        if (Boolean.TRUE.equals(cond.getNotifiableOnly())) {
            where.and(deviceEntity.fcmToken.isNotNull()).and(deviceEntity.accountId.isNotNull());
        }
        if (cond.getDeviceName() != null) where.and(deviceEntity.deviceName.eq(cond.getDeviceName()));
        if (cond.getAccountId() != null) where.and(deviceEntity.accountId.eq(cond.getAccountId()));
        return where;
    }

    @Override
    public List<DeviceEntity> findPage(DeviceSearchVo cond) {
        return jpaQueryFactory.selectFrom(deviceEntity)
                .where(searchCondition(cond))
                // created_at 은 기존 행에 NULL 일 수 있어 device_id 로 2차 정렬
                .orderBy(cond.isSortDesc() ? deviceEntity.createdAt.desc().nullsLast() : deviceEntity.createdAt.asc().nullsFirst(),
                        deviceEntity.deviceId.asc())
                .offset((long) cond.getPage() * cond.getSize())
                .limit(cond.getSize())
                .fetch();
    }

    @Override
    public long countPage(DeviceSearchVo cond) {
        Long count = jpaQueryFactory.select(deviceEntity.count())
                .from(deviceEntity)
                .where(searchCondition(cond))
                .fetchOne();
        return count == null ? 0 : count;
    }

    @Override
    public List<String> findDistinctDeviceNames() {
        return jpaQueryFactory.select(deviceEntity.deviceName).distinct()
                .from(deviceEntity)
                .where(deviceEntity.deviceName.isNotNull())
                .orderBy(deviceEntity.deviceName.asc())
                .fetch();
    }
}
