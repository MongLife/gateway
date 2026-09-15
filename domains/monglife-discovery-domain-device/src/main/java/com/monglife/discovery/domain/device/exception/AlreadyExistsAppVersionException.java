package com.monglife.discovery.domain.device.exception;

import com.monglife.core.exception.ErrorException;
import com.monglife.discovery.domain.device.enums.DeviceErrorCode;
import lombok.Getter;

import java.util.Map;

@Getter
public class AlreadyExistsAppVersionException extends ErrorException {

    public AlreadyExistsAppVersionException(String appPackageName, String buildVersion) {
        this.errorCode = DeviceErrorCode.DISCOVERY_DEVICE_ALREADY_EXISTS_APP_VERSION;
        this.result = Map.of("appPackageName", appPackageName, "buildVersion", buildVersion);
    }
}
