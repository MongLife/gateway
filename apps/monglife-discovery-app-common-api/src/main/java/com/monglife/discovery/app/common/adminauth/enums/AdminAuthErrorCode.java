package com.monglife.discovery.app.common.adminauth.enums;

import com.monglife.core.enums.error.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AdminAuthErrorCode implements ErrorCode {

    DISCOVERY_APP_ADMIN_AUTH_NOT_ADMIN_ACCOUNT("DISCOVERY-APP-ADMIN-AUTH-100", "관리자 권한이 있는 계정이 아닙니다."),
    DISCOVERY_APP_ADMIN_AUTH_TOO_MANY_REQUESTS("DISCOVERY-APP-ADMIN-AUTH-101", "잠시 후 다시 요청할 수 있습니다."),
    DISCOVERY_APP_ADMIN_AUTH_INVALID_CODE("DISCOVERY-APP-ADMIN-AUTH-102", "인증 코드가 올바르지 않습니다."),
    DISCOVERY_APP_ADMIN_AUTH_EXPIRED_CODE("DISCOVERY-APP-ADMIN-AUTH-103", "인증 코드가 만료되었습니다. 다시 요청하세요."),
    ;

    private final String code;

    private final String message;
}
