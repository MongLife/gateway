package com.monglife.discovery.domain.feedback.exception;

import com.monglife.core.exception.ErrorException;
import com.monglife.discovery.domain.feedback.enums.FeedbackErrorCode;
import lombok.Getter;

import java.util.Map;

@Getter
public class NotExistsFeedbackException extends ErrorException {

    public NotExistsFeedbackException(Long feedbackId) {
        this.errorCode = FeedbackErrorCode.DISCOVERY_FEEDBACK_NOT_EXISTS_FEEDBACK;
        this.result = Map.of("feedbackId", feedbackId);
    }
}
