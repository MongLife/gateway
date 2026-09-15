package com.monglife.discovery.app.common.global.mail;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * 관리자 이메일 인증 코드 · 오류 신고 답변 메일.
 * env.mail.enabled=false(local) 면 발송 대신 로그만 찍는다.
 */
@Slf4j
@Service
public class MailService {

    private final JavaMailSender javaMailSender;

    private final String from;

    private final boolean enabled;

    public MailService(
            JavaMailSender javaMailSender,
            @Value("${env.mail.from}") String from,
            @Value("${env.mail.enabled}") boolean enabled
    ) {
        this.javaMailSender = javaMailSender;
        this.from = from;
        this.enabled = enabled;
    }

    public void sendAdminEmailCode(String to, String code, long expirationSeconds) {
        String subject = "[MongLife Admin] 로그인 인증 코드";
        String body = """
                MongLife 관리자 로그인 인증 코드입니다.

                    %s

                %d분 안에 입력해 주세요. 본인이 요청하지 않았다면 이 메일을 무시하세요.
                """.formatted(code, expirationSeconds / 60);
        send(to, subject, body);
    }

    public void sendFeedbackReply(String to, String feedbackTitle, String content) {
        send(to, "Re: " + feedbackTitle, content);
    }

    private void send(String to, String subject, String body) {
        if (!enabled) {
            // local: 코드 확인용. 운영에서는 enabled=true 라 여기 안 온다
            log.info("[mail disabled] to={} subject={}\n{}", to, subject, body);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(from);
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            javaMailSender.send(message);
        } catch (MailException e) {
            log.error("mail send failed to={} subject={}", to, subject, e);
            throw new MailSendFailedException(to);
        }
    }
}
