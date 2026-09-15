package com.monglife.discovery.app.common.global.config;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.jwk.source.JWKSourceBuilder;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.DefaultJWTClaimsVerifier;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import com.nimbusds.jwt.proc.JWTProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class AppleIdTokenConfig {

    /**
     * Apple ID 토큰의 iss. Apple 이 정한 고정값이라 설정으로 빼지 않는다.
     * 환경별로 다를 여지가 있는 것은 aud(client-ids) 뿐이다.
     */
    private static final String APPLE_ISSUER = "https://appleid.apple.com";

    /**
     * Apple 공개키(JWKS) 엔드포인트. 마찬가지로 고정값이다.
     */
    private static final String APPLE_JWKS_URL = "https://appleid.apple.com/auth/keys";

    /**
     * Apple 공개키 소스
     * 캐싱 / 레이트리밋 / 재시도 / 장애 내성을 JWKSourceBuilder 가 처리한다.
     * 첫 요청 때 lazy 하게 가져오므로 기동 시점에 Apple 로 나가는 호출은 없다.
     * 테스트에서 정적 JWKSet 으로 갈아끼울 수 있도록 processor 와 분리된 빈으로 둔다.
     * @return Apple 공개키 소스
     */
    @Bean
    public JWKSource<SecurityContext> appleJwkSource() throws MalformedURLException {

        // 제네릭을 create 에 명시하지 않으면 체이닝이 타깃 타입 추론을 끊어 C 가 SecurityContext 로
        // 추론되지 않는다
        return JWKSourceBuilder.<SecurityContext>create(URI.create(APPLE_JWKS_URL).toURL())
                // 일시적 네트워크 오류를 한 번 재시도한다
                .retrying(true)
                // Apple JWKS 가 죽어도 캐시된 키로 계속 로그인시킨다
                .outageTolerant(true)
                .build();
    }

    /**
     * Apple ID 토큰 검증기
     * 서명 / iss / aud / exp 를 여기서 전부 처리하고, Provider 는 예외 매핑과 클레임 추출만 한다.
     * @param appleJwkSource Apple 공개키 소스
     * @param appleClientIds 허용 aud 목록 (콤마 구분). 네이티브 앱은 번들 ID 다
     * @return Apple ID 토큰 검증기
     */
    @Bean
    public JWTProcessor<SecurityContext> appleJwtProcessor(
            JWKSource<SecurityContext> appleJwkSource,
            @Value("${env.apple.oauth.client-ids}") List<String> appleClientIds
    ) {
        Assert.notEmpty(appleClientIds, "env.apple.oauth.client-ids must not be empty");

        DefaultJWTProcessor<SecurityContext> jwtProcessor = new DefaultJWTProcessor<>();

        // Apple 은 RS256 만 쓴다. 알고리즘을 고정해야 alg 혼동 공격을 막는다
        jwtProcessor.setJWSKeySelector(new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, appleJwkSource));

        jwtProcessor.setJWTClaimsSetVerifier(new DefaultJWTClaimsVerifier<>(
                new LinkedHashSet<>(appleClientIds),
                new JWTClaimsSet.Builder().issuer(APPLE_ISSUER).build(),
                // exp 는 "있으면" 검증된다. 필수로 못 박지 않으면 exp 없는 토큰이 무기한 통과한다
                Set.of("sub", "iss", "aud", "exp"),
                null));

        return jwtProcessor;
    }
}
