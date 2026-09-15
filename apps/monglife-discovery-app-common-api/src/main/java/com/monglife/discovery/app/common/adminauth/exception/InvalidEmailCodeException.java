package com.monglife.discovery.app.common.adminauth.exception;

import com.monglife.core.exception.ErrorException;
import com.monglife.discovery.app.common.adminauth.enums.AdminAuthErrorCode;
import lombok.Getter;

import java.util.Map;

@Getter
public class InvalidEmailCodeException extends ErrorException {

    public InvalidEmailCodeException(String email) {
        this.errorCode = AdminAuthErrorCode.DISCOVERY_APP_ADMIN_AUTH_INVALID_CODE;
        this.result = Map.of("email", email);
    }
}
