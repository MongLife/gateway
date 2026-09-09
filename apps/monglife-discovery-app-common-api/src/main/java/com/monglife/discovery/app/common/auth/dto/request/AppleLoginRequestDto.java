package com.monglife.discovery.app.common.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Apple 방식 로그인 요청 Dto
 * email 필드를 두지 않는다. 계정 조회는 검증된 identityToken 의 sub 로만 하고
 * 클라이언트도 로그인 요청에는 email 을 싣지 않는다.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppleLoginRequestDto {

    @NotEmpty
    @NotBlank
    private String socialAccountId;

    /**
     * 필드명이 idToken 이 아니다. iOS 가 보내는 이름 그대로다.
     */
    @NotEmpty
    @NotBlank
    private String identityToken;

    @NotEmpty
    @NotBlank
    private String deviceId;

    @NotEmpty
    @NotBlank
    private String appPackageName;

    @NotEmpty
    @NotBlank
    private String deviceName;

    @NotEmpty
    @NotBlank
    private String buildVersion;

    @Builder
    public AppleLoginRequestDto(String socialAccountId, String identityToken, String deviceId, String appPackageName, String deviceName, String buildVersion) {
        this.socialAccountId = socialAccountId;
        this.identityToken = identityToken;
        this.deviceId = deviceId;
        this.appPackageName = appPackageName;
        this.deviceName = deviceName;
        this.buildVersion = buildVersion;
    }
}
