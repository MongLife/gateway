package com.monglife.discovery.app.common.adminauth.exception;

import com.monglife.core.exception.ErrorException;
import com.monglife.discovery.app.common.adminauth.enums.AdminAuthErrorCode;
import lombok.Getter;

import java.util.Map;

@Getter
public class TooManyEmailCodeRequestsException extends ErrorException {

    public TooManyEmailCodeRequestsException(long retryAfterSeconds) {
        this.errorCode = AdminAuthErrorCode.DISCOVERY_APP_ADMIN_AUTH_TOO_MANY_REQUESTS;
        this.result = Map.of("retryAfterSeconds", retryAfterSeconds);
    }
}
