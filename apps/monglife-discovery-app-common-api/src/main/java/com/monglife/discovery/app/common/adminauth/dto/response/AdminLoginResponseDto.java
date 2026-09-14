package com.monglife.discovery.app.common.adminauth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AdminLoginResponseDto {

    private final Long accountId;

    private final String accessToken;

    private final String refreshToken;

    @Builder
    public AdminLoginResponseDto(Long accountId, String accessToken, String refreshToken) {
        this.accountId = accountId;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
