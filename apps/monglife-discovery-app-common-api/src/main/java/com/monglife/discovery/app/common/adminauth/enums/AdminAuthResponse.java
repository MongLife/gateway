package com.monglife.discovery.app.common.adminauth.enums;

import com.monglife.core.enums.response.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum AdminAuthResponse implements Response {

    DISCOVERY_APP_ADMIN_AUTH_EMAIL_CODE(HttpStatus.OK.value(), "DISCOVERY-APP-ADMIN-AUTH-000", "인증 코드를 발송하였습니다."),
    DISCOVERY_APP_ADMIN_AUTH_EMAIL_VERIFY(HttpStatus.OK.value(), "DISCOVERY-APP-ADMIN-AUTH-001", "관리자 로그인에 성공하였습니다."),
    ;

    private final Integer httpStatus;

    private final String code;

    private final String message;
}
