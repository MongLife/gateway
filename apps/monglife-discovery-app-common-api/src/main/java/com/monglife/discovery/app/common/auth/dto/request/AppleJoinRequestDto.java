package com.monglife.discovery.app.common.auth.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppleJoinRequestDto {

    @NotEmpty
    @NotBlank
    private String socialAccountId;

    /**
     * 필드명이 idToken 이 아니다. iOS 가 보내는 이름 그대로다.
     */
    @NotEmpty
    @NotBlank
    private String identityToken;

    /**
     * 선택값. 클라이언트는 값이 없으면 키 자체를 생략한다.
     * 검증 애노테이션을 붙이면 정상 요청이 400 이 되므로 무제약으로 둔다.
     * 계정 email 은 sub 기반 placeholder 로 만들기 때문에 이 값은 저장하지 않는다.
     */
    private String email;

    /**
     * 선택값. Apple 은 이름을 최초 인가 때 한 번만 내려주고 재로그인 시엔 주지 않는다.
     * 그래서 이 필드가 이름의 유일한 출처다.
     */
    private String name;

    @Builder
    public AppleJoinRequestDto(String socialAccountId, String identityToken, String email, String name) {
        this.socialAccountId = socialAccountId;
        this.identityToken = identityToken;
        this.email = email;
        this.name = name;
    }
}
