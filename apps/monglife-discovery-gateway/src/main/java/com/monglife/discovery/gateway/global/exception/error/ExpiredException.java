package com.monglife.discovery.gateway.global.exception.error;

import com.monglife.core.code.ErrorCode;
import com.monglife.core.exception.ErrorException;

public class ExpiredException extends ErrorException {
    public ExpiredException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ExpiredException(Throwable e) {
        super(e);
    }

    public ExpiredException(ErrorCode errorCode, Throwable e) {
        super(errorCode, e);
    }
}
