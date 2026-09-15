package com.monglife.discovery.domain.feedback.entity;

import com.monglife.discovery.domain.feedback.enums.FeedbackStatus;
import com.monglife.module.common.jpa.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 사용자 오류 신고. monglife-mongs 의 mongs_feedback 을 Discovery 로 옮긴 것에
 * 앱 패키지·버전(토큰에서), 상태, 관리자 답변(1회, 재답변은 덮어씀) 을 더했다.
 * stg/prd 는 hbm2ddl 이 none 이라 수동 DDL 이 필요하다 (configs/migration 참고).
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "monglife_feedback",
        indexes = {
                // columnList 는 논리 컬럼명 — AccountEntity 주석 참고. created_at 은 BaseTimeEntity 가 @Column(name) 을 준 물리명이라 그대로 쓴다
                @Index(name = "idx_monglife_feedback_account_id", columnList = "accountId"),
                @Index(name = "idx_monglife_feedback_status_created_at", columnList = "status, created_at")
        }
)
public class FeedbackEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long feedbackId;

    @Column(nullable = false, updatable = false)
    private Long accountId;

    @Column(updatable = false)
    private String deviceId;

    @Column
    private String deviceName;

    @Column(updatable = false)
    private String appPackageName;

    @Column(updatable = false)
    private String buildVersion;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, length = 16)
    private String status = FeedbackStatus.OPEN.name();

    @Column(columnDefinition = "TEXT")
    private String replyContent;

    /** 답변 메일을 보낸 주소 */
    @Column
    private String replySentTo;

    /** 답변한 관리자 계정 ID */
    @Column
    private Long replyAccountId;

    @Column
    private LocalDateTime repliedAt;

    @Builder
    public FeedbackEntity(Long accountId, String deviceId, String deviceName, String appPackageName, String buildVersion, String title, String content) {
        this.accountId = accountId;
        this.deviceId = deviceId;
        this.deviceName = deviceName;
        this.appPackageName = appPackageName;
        this.buildVersion = buildVersion;
        this.title = title;
        this.content = content;
        this.status = FeedbackStatus.OPEN.name();
    }

    public void reply(String content, String sentTo, Long adminAccountId) {
        this.replyContent = content;
        this.replySentTo = sentTo;
        this.replyAccountId = adminAccountId;
        this.repliedAt = LocalDateTime.now();
        this.status = FeedbackStatus.ANSWERED.name();
    }
}
