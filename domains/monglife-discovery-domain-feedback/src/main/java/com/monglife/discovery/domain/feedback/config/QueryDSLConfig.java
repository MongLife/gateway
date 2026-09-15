package com.monglife.discovery.domain.feedback.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// account·device 모듈에도 QueryDSLConfig 가 있어 빈 이름이 충돌하므로 이름을 명시한다
@Configuration("feedbackQueryDSLConfig")
public class QueryDSLConfig {

    @Bean(name = "feedbackJpaQueryFactory")
    public JPAQueryFactory jpaQueryFactory(@Qualifier("feedbackEntityManager") EntityManager entityManager) {
        return new JPAQueryFactory(entityManager);
    }
}
