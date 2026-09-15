package com.monglife.discovery.app.common.adminauth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AdminEmailCodeResponseDto {

    private final long expiresIn;

    private final long resendAfter;

    @Builder
    public AdminEmailCodeResponseDto(long expiresIn, long resendAfter) {
        this.expiresIn = expiresIn;
        this.resendAfter = resendAfter;
    }
}
