package com.monglife.discovery.app.common.admin.service;

import com.monglife.discovery.app.common.admin.dto.response.AdminFeedbackReplyResponseDto;
import com.monglife.discovery.app.common.global.mail.MailSendFailedException;
import com.monglife.discovery.app.common.global.mail.MailService;
import com.monglife.discovery.domain.account.service.AccountService;
import com.monglife.discovery.domain.account.vo.AccountVo;
import com.monglife.discovery.domain.feedback.service.FeedbackService;
import com.monglife.discovery.domain.feedback.vo.FeedbackVo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("AdminFeedbackService 답변")
class AdminFeedbackServiceTest {

    @Mock private FeedbackService feedbackService;
    @Mock private AccountService accountService;
    @Mock private AdminAccountService adminAccountService;
    @Mock private MailService mailService;
    @InjectMocks private AdminFeedbackService adminFeedbackService;

    @BeforeEach
    void setUp() {
        given(feedbackService.getFeedback(7L)).willReturn(FeedbackVo.builder().feedbackId(7L).accountId(3L).title("앱이 꺼져요").content("...").status("OPEN").build());
        given(accountService.getAccountIncludingDeleted(3L)).willReturn(AccountVo.builder().accountId(3L).email("user3@example.com").name("김몽").build());
        given(feedbackService.reply(eq7(), anyString(), anyString(), anyLong())).willAnswer(inv -> FeedbackVo.builder()
                .feedbackId(7L).accountId(3L).title("앱이 꺼져요").content("...").status("ANSWERED")
                .replyContent(inv.getArgument(1)).replySentTo(inv.getArgument(2)).replyAccountId(inv.getArgument(3)).repliedAt(LocalDateTime.now())
                .build());
    }

    private static long eq7() {
        return org.mockito.ArgumentMatchers.eq(7L);
    }

    @Test
    @DisplayName("메일을 보내고 상태를 ANSWERED 로 바꾼다")
    void reply_success() {
        AdminFeedbackReplyResponseDto reply = adminFeedbackService.reply(7L, "확인했습니다.", 1L);

        verify(mailService, times(1)).sendFeedbackReply("user3@example.com", "앱이 꺼져요", "확인했습니다.");
        verify(feedbackService, times(1)).reply(7L, "확인했습니다.", "user3@example.com", 1L);
        assertThat(reply.getSentTo()).isEqualTo("user3@example.com");
        assertThat(reply.getContent()).isEqualTo("확인했습니다.");
    }

    @Test
    @DisplayName("메일 발송이 실패하면 답변을 저장하지 않는다")
    void reply_mailFailed() {
        willThrow(new MailSendFailedException("user3@example.com")).given(mailService).sendFeedbackReply(anyString(), anyString(), anyString());

        assertThatThrownBy(() -> adminFeedbackService.reply(7L, "확인했습니다.", 1L)).isInstanceOf(MailSendFailedException.class);
        verify(feedbackService, never()).reply(anyLong(), any(), any(), any());
    }
}
