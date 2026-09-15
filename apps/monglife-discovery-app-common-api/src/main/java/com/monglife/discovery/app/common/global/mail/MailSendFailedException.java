package com.monglife.discovery.app.common.global.mail;

import com.monglife.core.exception.ErrorException;
import com.monglife.discovery.app.common.global.enums.GlobalErrorCode;
import lombok.Getter;

import java.util.Map;

@Getter
public class MailSendFailedException extends ErrorException {

    public MailSendFailedException(String to) {
        this.errorCode = GlobalErrorCode.DISCOVERY_APP_MAIL_SEND_FAILED;
        this.result = Map.of("to", to);
    }
}
