package com.monglife.discovery.app.common.admin.service;

import com.monglife.discovery.app.common.admin.dto.response.AdminFeedbackReplyResponseDto;
import com.monglife.discovery.app.common.admin.dto.response.AdminFeedbackResponseDto;
import com.monglife.discovery.app.common.admin.util.AdminPage;
import com.monglife.discovery.app.common.admin.util.SortSpec;
import com.monglife.discovery.app.common.global.mail.MailService;
import com.monglife.discovery.domain.account.service.AccountService;
import com.monglife.discovery.domain.account.vo.AccountVo;
import com.monglife.discovery.domain.feedback.service.FeedbackService;
import com.monglife.discovery.domain.feedback.vo.FeedbackSearchVo;
import com.monglife.discovery.domain.feedback.vo.FeedbackVo;
import com.monglife.discovery.domain.feedback.vo.PageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AdminFeedbackService {

    public static final Set<String> SORT_KEYS = Set.of("reportId", "createdAt");

    private final FeedbackService feedbackService;
    private final AccountService accountService;
    private final AdminAccountService adminAccountService;
    private final MailService mailService;

    @Transactional(readOnly = true)
    public AdminPage<AdminFeedbackResponseDto> getFeedbacks(String query, String status, String deviceName, String appPackageName, String buildVersion, int page, int size, SortSpec sort) {
        PageVo<FeedbackVo> result = feedbackService.getFeedbacks(FeedbackSearchVo.builder()
                .query(query)
                .queryAccountIds(adminAccountService.accountIdsByQuery(query))
                .status(status)
                .deviceName(deviceName)
                .appPackageName(appPackageName)
                .buildVersion(buildVersion)
                .page(page)
                .size(size)
                // 화면은 reportId 로 부르고 엔티티는 feedbackId 다
                .sortKey("reportId".equals(sort.key()) ? "feedbackId" : sort.key())
                .sortDesc(sort.desc())
                .build());
        Map<Long, AccountVo> accounts = adminAccountService.accountMap(result.getItems().stream().map(FeedbackVo::getAccountId).distinct().toList());
        return new AdminPage<>(result.getItems().stream().map(f -> AdminMapper.feedback(f, accounts, false)).toList(),
                page, size, result.getTotal());
    }

    @Transactional(readOnly = true)
    public AdminFeedbackResponseDto getFeedback(Long feedbackId) {
        FeedbackVo f = feedbackService.getFeedback(feedbackId);
        return AdminMapper.feedback(f, adminAccountService.accountMap(java.util.List.of(f.getAccountId())), true);
    }

    /**
     * 답변: 신고자 이메일로 메일을 보내고 상태를 ANSWERED 로 바꾼다. 메일이 실패하면 저장하지 않는다.
     */
    @Transactional
    public AdminFeedbackReplyResponseDto reply(Long feedbackId, String content, Long adminAccountId) {
        FeedbackVo f = feedbackService.getFeedback(feedbackId);
        AccountVo reporter = accountService.getAccountIncludingDeleted(f.getAccountId());

        mailService.sendFeedbackReply(reporter.getEmail(), f.getTitle(), content);

        FeedbackVo replied = feedbackService.reply(feedbackId, content, reporter.getEmail(), adminAccountId);
        return AdminFeedbackReplyResponseDto.builder()
                .content(replied.getReplyContent())
                .sentTo(replied.getReplySentTo())
                .createdAt(replied.getRepliedAt())
                .build();
    }
}
