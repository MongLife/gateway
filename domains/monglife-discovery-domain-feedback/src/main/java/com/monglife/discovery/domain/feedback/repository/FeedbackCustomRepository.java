package com.monglife.discovery.domain.feedback.repository;

import com.monglife.discovery.domain.feedback.entity.FeedbackEntity;
import com.monglife.discovery.domain.feedback.vo.FeedbackSearchVo;

import java.util.List;

public interface FeedbackCustomRepository {

    List<FeedbackEntity> findPage(FeedbackSearchVo cond);

    long countPage(FeedbackSearchVo cond);
}
