package com.monglife.discovery.app.common.global.enums;

import com.monglife.core.enums.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GlobalErrorCode implements ErrorCode {

    DISCOVERY_APP_MAIL_SEND_FAILED("DISCOVERY-APP-GLOBAL-100", "메일 발송에 실패했습니다."),
    ;

    private final String code;

    private final String message;
}
