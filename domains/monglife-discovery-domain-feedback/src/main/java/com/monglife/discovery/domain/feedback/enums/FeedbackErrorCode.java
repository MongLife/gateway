package com.monglife.discovery.domain.feedback.enums;

import com.monglife.core.enums.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FeedbackErrorCode implements ErrorCode {

    DISCOVERY_FEEDBACK_NOT_EXISTS_FEEDBACK("DISCOVERY-FEEDBACK-100", "오류 신고가 존재하지 않습니다."),
    ;

    private final String code;

    private final String message;
}
