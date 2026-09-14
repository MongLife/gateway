package com.monglife.discovery.domain.feedback.service;

import com.monglife.discovery.domain.feedback.entity.FeedbackEntity;
import com.monglife.discovery.domain.feedback.exception.NotExistsFeedbackException;
import com.monglife.discovery.domain.feedback.repository.FeedbackRepository;
import com.monglife.discovery.domain.feedback.vo.FeedbackSearchVo;
import com.monglife.discovery.domain.feedback.vo.FeedbackVo;
import com.monglife.discovery.domain.feedback.vo.PageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;

    static FeedbackVo toVo(FeedbackEntity e) {
        return FeedbackVo.builder()
                .feedbackId(e.getFeedbackId())
                .accountId(e.getAccountId())
                .deviceId(e.getDeviceId())
                .deviceName(e.getDeviceName())
                .appPackageName(e.getAppPackageName())
                .buildVersion(e.getBuildVersion())
                .title(e.getTitle())
                .content(e.getContent())
                .status(e.getStatus())
                .replyContent(e.getReplyContent())
                .replySentTo(e.getReplySentTo())
                .replyAccountId(e.getReplyAccountId())
                .repliedAt(e.getRepliedAt())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    /**
     * 오류 신고 등록 (앱)
     */
    @Transactional
    public FeedbackVo createFeedback(FeedbackVo vo) {
        return toVo(feedbackRepository.save(FeedbackEntity.builder()
                .accountId(vo.getAccountId())
                .deviceId(vo.getDeviceId())
                .deviceName(vo.getDeviceName())
                .appPackageName(vo.getAppPackageName())
                .buildVersion(vo.getBuildVersion())
                .title(vo.getTitle())
                .content(vo.getContent())
                .build()));
    }

    @Transactional(readOnly = true)
    public FeedbackVo getFeedback(Long feedbackId) {
        return toVo(feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new NotExistsFeedbackException(feedbackId)));
    }

    @Transactional(readOnly = true)
    public PageVo<FeedbackVo> getFeedbacks(FeedbackSearchVo cond) {
        return PageVo.<FeedbackVo>builder()
                .items(feedbackRepository.findPage(cond).stream().map(FeedbackService::toVo).toList())
                .page(cond.getPage())
                .size(cond.getSize())
                .total(feedbackRepository.countPage(cond))
                .build();
    }

    /**
     * 관리자 답변. 이미 답변된 건은 덮어쓴다.
     */
    @Transactional
    public FeedbackVo reply(Long feedbackId, String content, String sentTo, Long adminAccountId) {
        FeedbackEntity e = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new NotExistsFeedbackException(feedbackId));
        e.reply(content, sentTo, adminAccountId);
        return toVo(e);
    }
}
