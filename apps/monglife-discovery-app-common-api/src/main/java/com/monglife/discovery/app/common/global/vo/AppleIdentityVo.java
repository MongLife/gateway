package com.monglife.discovery.app.common.global.vo;

import lombok.Builder;
import lombok.Getter;

@Getter
public class AppleIdentityVo {

    /**
     * Apple sub. Apple 경로의 유일한 계정 키다.
     * AppleIdTokenProvider 가 비어 있으면 거부하므로 여기서는 non-null 이 보장된다.
     */
    private final String socialAccountId;

    /**
     * nullable. 사용자가 이메일 공유를 거부하면 클레임 자체가 없고,
     * "이메일 가리기" 를 고르면 xxxxx@privaterelay.appleid.com 으로 온다.
     */
    private final String email;

    /**
     * nullable. Apple 이 boolean 이 아니라 문자열 "true" 로 보낼 때가 있어 정규화한 값이다.
     */
    private final Boolean emailVerified;

    @Builder
    public AppleIdentityVo(String socialAccountId, String email, Boolean emailVerified) {
        this.socialAccountId = socialAccountId;
        this.email = email;
        this.emailVerified = emailVerified;
    }
}
