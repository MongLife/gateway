package com.monglife.discovery.domain.feedback.repository;

import com.monglife.discovery.domain.feedback.entity.FeedbackEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeedbackRepository extends JpaRepository<FeedbackEntity, Long>, FeedbackCustomRepository {
}
