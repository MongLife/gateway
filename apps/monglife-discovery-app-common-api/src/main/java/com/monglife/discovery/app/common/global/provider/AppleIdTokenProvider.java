package com.monglife.discovery.app.common.global.provider;

import com.monglife.discovery.app.common.auth.exception.IdTokenVerifyFailedException;
import com.monglife.discovery.app.common.auth.exception.InvalidIdTokenException;
import com.monglife.discovery.app.common.global.vo.AppleIdentityVo;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.KeySourceException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.JWTProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.text.ParseException;

@Component
@RequiredArgsConstructor
public class AppleIdTokenProvider {

    private final JWTProcessor<SecurityContext> appleJwtProcessor;

    /**
     * Apple ID 토큰 검증
     * 서명 / iss / aud / exp 는 AppleIdTokenConfig 가 조립한 검증기가 처리하고,
     * 계정 키로 쓰는 sub 의 존재 여부를 여기서 추가로 확인한다.
     * GoogleIdTokenProvider 와 같이 공개키 fetch 실패(일시 장애)와 토큰이 잘못됨(클라이언트 잘못)을 구분한다.
     * @param identityToken Apple ID 토큰
     * @return 검증된 Apple 계정 정보
     */
    public AppleIdentityVo verify(String identityToken) {

        JWTClaimsSet claims;

        try {
            claims = appleJwtProcessor.process(identityToken, null);

        } catch (KeySourceException e) {
            // JWKS fetch 실패 등 서버 측 일시 장애. "토큰이 잘못됨" 과 구분한다.
            // KeySourceException 은 JOSEException 의 하위라 반드시 먼저 잡아야 컴파일된다
            throw new IdTokenVerifyFailedException();

        } catch (JOSEException e) {
            // 서명 검증을 "수행하지 못한" 경우(알고리즘 미지원, 크립토 프로바이더 오류).
            // 서명이 "틀린" 경우는 BadJWSException 으로 와서 아래로 간다
            throw new IdTokenVerifyFailedException();

        } catch (ParseException | BadJOSEException e) {
            // ParseException   : JWT 형식이 아님
            // BadJWSException  : 서명 불일치
            // BadJWTException  : iss/aud 불일치, exp 만료, 필수 클레임 누락
            // BadJOSEException : kid 매칭 실패, 평문(alg=none)/암호화 JWT 거부
            // BadJOSEException 은 JOSEException 의 하위가 아니라 형제다
            throw new InvalidIdTokenException();

        } catch (RuntimeException e) {
            // 신뢰할 수 없는 입력을 파싱하는 구간이라 예상 못 한 런타임 예외가 500 으로 나가지 않게 막는다
            throw new InvalidIdTokenException();
        }

        String socialAccountId = claims.getSubject();

        if (socialAccountId == null || socialAccountId.isBlank()) {
            throw new InvalidIdTokenException();
        }

        // email_verified 는 검증 게이트로 쓰지 않는다. Apple 은 sub 로만 계정을 특정하므로 email 은
        // 참고값이고, 게이트로 쓰면 이메일을 숨긴 사용자가 전원 로그인 불가가 된다
        return AppleIdentityVo.builder()
                .socialAccountId(socialAccountId)
                .email(stringClaim(claims, "email"))
                .emailVerified(booleanLikeClaim(claims, "email_verified"))
                .build();
    }

    /**
     * 선택 클레임을 문자열로 읽는다
     * getStringClaim 은 값이 문자열이 아니면 ParseException 을 던져 선택 클레임 읽기에 쓸 수 없다.
     */
    private static String stringClaim(JWTClaimsSet claims, String name) {

        Object value = claims.getClaim(name);

        return (value instanceof String string && !string.isBlank()) ? string : null;
    }

    /**
     * 선택 클레임을 boolean 으로 읽는다
     * Apple 은 email_verified 를 boolean 이 아니라 문자열 "true" 로 보낼 때가 있다.
     * getBooleanClaim 을 쓰면 그 경우 ParseException 이 나서 정상 토큰이 전부 거부된다.
     */
    private static Boolean booleanLikeClaim(JWTClaimsSet claims, String name) {

        Object value = claims.getClaim(name);

        if (value instanceof Boolean bool) {
            return bool;
        }

        if (value instanceof String string) {
            return Boolean.parseBoolean(string);
        }

        return null;
    }
}
