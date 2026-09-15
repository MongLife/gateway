package com.monglife.discovery.app.common.feedback.enums;

import com.monglife.core.enums.response.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum FeedbackResponse implements Response {

    DISCOVERY_APP_FEEDBACK_CREATE(HttpStatus.OK.value(), "DISCOVERY-APP-FEEDBACK-000", "오류 신고 등록에 성공했습니다."),
    ;

    private final Integer httpStatus;

    private final String code;

    private final String message;
}
