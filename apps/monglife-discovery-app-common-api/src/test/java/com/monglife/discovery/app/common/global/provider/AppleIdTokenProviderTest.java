package com.monglife.discovery.app.common.global.provider;

import com.monglife.discovery.app.common.auth.exception.IdTokenVerifyFailedException;
import com.monglife.discovery.app.common.auth.exception.InvalidIdTokenException;
import com.monglife.discovery.app.common.global.config.AppleIdTokenConfig;
import com.monglife.discovery.app.common.global.vo.AppleIdentityVo;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.RemoteKeySourceException;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.gen.RSAKeyGenerator;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.BadJWSException;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.nimbusds.jwt.proc.BadJWTException;
import com.nimbusds.jwt.proc.JWTProcessor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.ParseException;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@DisplayName("AppleIdTokenProvider")
class AppleIdTokenProviderTest {

    private static final String IDENTITY_TOKEN = "identity-token";
    private static final String SUB = "001234.a1b2c3d4e5f6.1234";
    private static final String EMAIL = "abc123@privaterelay.appleid.com";
    private static final String ISSUER = "https://appleid.apple.com";
    private static final String AUDIENCE = "com.mongs.wear";

    /**
     * 검증기가 던지는 것을 어떤 예외로 옮기는지만 본다.
     * 공개키 fetch 실패(일시 장애)와 토큰이 잘못됨(클라이언트 잘못)의 구분이 핵심이다.
     */
    @Nested
    @ExtendWith(MockitoExtension.class)
    @DisplayName("예외 매핑")
    class ExceptionMapping {

        @Mock private JWTProcessor<SecurityContext> appleJwtProcessor;

        @InjectMocks private AppleIdTokenProvider appleIdTokenProvider;

        private void givenClaims(JWTClaimsSet claims) throws Exception {
            given(appleJwtProcessor.process(IDENTITY_TOKEN, null)).willReturn(claims);
        }

        private void givenThrows(Throwable throwable) throws Exception {
            given(appleJwtProcessor.process(IDENTITY_TOKEN, null)).willThrow(throwable);
        }

        @Test
        @DisplayName("정상 토큰 - sub/email 을 매핑한다")
        void verify_success() throws Exception {

            givenClaims(new JWTClaimsSet.Builder()
                    .subject(SUB)
                    .claim("email", EMAIL)
                    .claim("email_verified", true)
                    .build());

            AppleIdentityVo result = appleIdTokenProvider.verify(IDENTITY_TOKEN);

            assertThat(result.getSocialAccountId()).isEqualTo(SUB);
            assertThat(result.getEmail()).isEqualTo(EMAIL);
            assertThat(result.getEmailVerified()).isTrue();
        }

        @Test
        @DisplayName("email_verified 가 문자열 \"true\" 여도 통과한다 - getBooleanClaim 을 쓰면 여기서 전부 깨진다")
        void verify_emailVerifiedAsString() throws Exception {

            givenClaims(new JWTClaimsSet.Builder()
                    .subject(SUB)
                    .claim("email", EMAIL)
                    .claim("email_verified", "true")
                    .build());

            AppleIdentityVo result = appleIdTokenProvider.verify(IDENTITY_TOKEN);

            assertThat(result.getEmailVerified()).isTrue();
            assertThat(result.getEmail()).isEqualTo(EMAIL);
        }

        @Test
        @DisplayName("email 클레임이 없어도 통과하고 email 은 null 이다 - 이메일 숨김 / 재로그인")
        void verify_withoutEmail() throws Exception {

            givenClaims(new JWTClaimsSet.Builder().subject(SUB).build());

            AppleIdentityVo result = appleIdTokenProvider.verify(IDENTITY_TOKEN);

            assertThat(result.getEmail()).isNull();
            assertThat(result.getEmailVerified()).isNull();
            assertThat(result.getSocialAccountId()).isEqualTo(SUB);
        }

        @Test
        @DisplayName("email 이 빈 문자열이면 null 로 본다")
        void verify_blankEmail() throws Exception {

            givenClaims(new JWTClaimsSet.Builder().subject(SUB).claim("email", "  ").build());

            assertThat(appleIdTokenProvider.verify(IDENTITY_TOKEN).getEmail()).isNull();
        }

        @Test
        @DisplayName("email_verified 가 false 여도 거부하지 않는다 - 계정 키는 sub 다")
        void verify_emailNotVerified() throws Exception {

            givenClaims(new JWTClaimsSet.Builder()
                    .subject(SUB)
                    .claim("email", EMAIL)
                    .claim("email_verified", false)
                    .build());

            AppleIdentityVo result = appleIdTokenProvider.verify(IDENTITY_TOKEN);

            assertThat(result.getEmailVerified()).isFalse();
            assertThat(result.getSocialAccountId()).isEqualTo(SUB);
        }

        @Test
        @DisplayName("sub 가 없으면 거부한다")
        void verify_noSubject() throws Exception {

            givenClaims(new JWTClaimsSet.Builder().claim("email", EMAIL).build());

            assertThatThrownBy(() -> appleIdTokenProvider.verify(IDENTITY_TOKEN))
                    .isInstanceOf(InvalidIdTokenException.class);
        }

        @Test
        @DisplayName("sub 가 공백이면 거부한다")
        void verify_blankSubject() throws Exception {

            givenClaims(new JWTClaimsSet.Builder().subject("   ").build());

            assertThatThrownBy(() -> appleIdTokenProvider.verify(IDENTITY_TOKEN))
                    .isInstanceOf(InvalidIdTokenException.class);
        }

        @Test
        @DisplayName("JWKS fetch 실패는 서버 측 일시 장애로 구분한다")
        void verify_remoteKeySourceException() throws Exception {

            givenThrows(new RemoteKeySourceException("jwks unreachable", null));

            assertThatThrownBy(() -> appleIdTokenProvider.verify(IDENTITY_TOKEN))
                    .isInstanceOf(IdTokenVerifyFailedException.class);
        }

        @Test
        @DisplayName("서명 검증을 수행하지 못하면 서버 측 일시 장애로 구분한다")
        void verify_joseException() throws Exception {

            givenThrows(new JOSEException("unsupported algorithm"));

            assertThatThrownBy(() -> appleIdTokenProvider.verify(IDENTITY_TOKEN))
                    .isInstanceOf(IdTokenVerifyFailedException.class);
        }

        @Test
        @DisplayName("JWT 형식이 아니면 InvalidIdTokenException")
        void verify_parseException() throws Exception {

            givenThrows(new ParseException("malformed", 0));

            assertThatThrownBy(() -> appleIdTokenProvider.verify(IDENTITY_TOKEN))
                    .isInstanceOf(InvalidIdTokenException.class);
        }

        @Test
        @DisplayName("서명 불일치는 InvalidIdTokenException")
        void verify_badJwsException() throws Exception {

            givenThrows(new BadJWSException("Signed JWT rejected: Invalid signature"));

            assertThatThrownBy(() -> appleIdTokenProvider.verify(IDENTITY_TOKEN))
                    .isInstanceOf(InvalidIdTokenException.class);
        }

        @Test
        @DisplayName("iss/aud 불일치·만료는 InvalidIdTokenException")
        void verify_badJwtException() throws Exception {

            givenThrows(new BadJWTException("Expired JWT"));

            assertThatThrownBy(() -> appleIdTokenProvider.verify(IDENTITY_TOKEN))
                    .isInstanceOf(InvalidIdTokenException.class);
        }

        @Test
        @DisplayName("매칭되는 키가 없으면 InvalidIdTokenException")
        void verify_badJoseException() throws Exception {

            givenThrows(new BadJOSEException("Signed JWT rejected: no matching key"));

            assertThatThrownBy(() -> appleIdTokenProvider.verify(IDENTITY_TOKEN))
                    .isInstanceOf(InvalidIdTokenException.class);
        }

        @Test
        @DisplayName("검증기가 NPE 를 던져도 500 이 아니라 InvalidIdTokenException 이어야 한다")
        void verify_npeFromProcessor() throws Exception {

            givenThrows(new NullPointerException());

            assertThatThrownBy(() -> appleIdTokenProvider.verify(IDENTITY_TOKEN))
                    .isInstanceOf(InvalidIdTokenException.class);
        }
    }

    /**
     * JWKS 만 정적 키셋으로 갈아끼우고 나머지는 운영과 완전히 같은 검증기로 돌린다.
     * AppleIdTokenConfig 가 JWKSource 와 JWTProcessor 를 별도 빈으로 나눠 둔 이유가 이것이다.
     */
    @Nested
    @DisplayName("실 검증기 (JWKS 만 정적 키셋)")
    class RealProcessor {

        private RSAKey rsaKey;
        private AppleIdTokenProvider appleIdTokenProvider;

        @BeforeEach
        void setUp() throws Exception {

            rsaKey = new RSAKeyGenerator(2048).keyID("test-kid").generate();

            JWKSource<SecurityContext> jwkSource = new ImmutableJWKSet<>(new JWKSet(rsaKey.toPublicJWK()));

            appleIdTokenProvider = new AppleIdTokenProvider(
                    new AppleIdTokenConfig().appleJwtProcessor(jwkSource, List.of(AUDIENCE)));
        }

        private JWTClaimsSet.Builder validClaims() {
            return new JWTClaimsSet.Builder()
                    .issuer(ISSUER)
                    .audience(AUDIENCE)
                    .subject(SUB)
                    .issueTime(new Date())
                    .expirationTime(Date.from(Instant.now().plusSeconds(600)));
        }

        private String sign(RSAKey key, String keyId, JWTClaimsSet claims) throws Exception {

            SignedJWT signedJWT = new SignedJWT(
                    new JWSHeader.Builder(JWSAlgorithm.RS256).keyID(keyId).build(), claims);

            signedJWT.sign(new RSASSASigner(key));

            return signedJWT.serialize();
        }

        private String sign(JWTClaimsSet claims) throws Exception {
            return sign(rsaKey, rsaKey.getKeyID(), claims);
        }

        @Test
        @DisplayName("정상 토큰을 통과시킨다")
        void verify_success() throws Exception {

            AppleIdentityVo result = appleIdTokenProvider.verify(sign(validClaims().build()));

            assertThat(result.getSocialAccountId()).isEqualTo(SUB);
        }

        @Test
        @DisplayName("회귀: email_verified 가 문자열 \"true\" 인 실제 형태의 토큰이 통과한다")
        void verify_emailVerifiedAsString() throws Exception {

            String token = sign(validClaims()
                    .claim("email", EMAIL)
                    .claim("email_verified", "true")
                    .claim("is_private_email", "true")
                    .build());

            AppleIdentityVo result = appleIdTokenProvider.verify(token);

            assertThat(result.getEmail()).isEqualTo(EMAIL);
            assertThat(result.getEmailVerified()).isTrue();
        }

        @Test
        @DisplayName("만료된 토큰은 거부한다")
        void verify_expired() throws Exception {

            String token = sign(validClaims()
                    .expirationTime(Date.from(Instant.now().minusSeconds(3600)))
                    .build());

            assertThatThrownBy(() -> appleIdTokenProvider.verify(token))
                    .isInstanceOf(InvalidIdTokenException.class);
        }

        @Test
        @DisplayName("exp 클레임이 없는 토큰은 거부한다 - 없으면 무기한 유효가 된다")
        void verify_withoutExpiration() throws Exception {

            String token = sign(new JWTClaimsSet.Builder()
                    .issuer(ISSUER)
                    .audience(AUDIENCE)
                    .subject(SUB)
                    .build());

            assertThatThrownBy(() -> appleIdTokenProvider.verify(token))
                    .isInstanceOf(InvalidIdTokenException.class);
        }

        @Test
        @DisplayName("aud 가 다른 앱이면 거부한다")
        void verify_wrongAudience() throws Exception {

            String token = sign(validClaims().audience("com.other.app").build());

            assertThatThrownBy(() -> appleIdTokenProvider.verify(token))
                    .isInstanceOf(InvalidIdTokenException.class);
        }

        @Test
        @DisplayName("iss 가 Apple 이 아니면 거부한다")
        void verify_wrongIssuer() throws Exception {

            String token = sign(validClaims().issuer("https://evil.example").build());

            assertThatThrownBy(() -> appleIdTokenProvider.verify(token))
                    .isInstanceOf(InvalidIdTokenException.class);
        }

        @Test
        @DisplayName("kid 는 맞지만 다른 키로 서명하면 거부한다")
        void verify_wrongSigningKey() throws Exception {

            RSAKey attackerKey = new RSAKeyGenerator(2048).keyID("test-kid").generate();

            String token = sign(attackerKey, "test-kid", validClaims().build());

            assertThatThrownBy(() -> appleIdTokenProvider.verify(token))
                    .isInstanceOf(InvalidIdTokenException.class);
        }

        @Test
        @DisplayName("알 수 없는 kid 는 거부한다")
        void verify_unknownKeyId() throws Exception {

            String token = sign(rsaKey, "unknown-kid", validClaims().build());

            assertThatThrownBy(() -> appleIdTokenProvider.verify(token))
                    .isInstanceOf(InvalidIdTokenException.class);
        }

        @Test
        @DisplayName("회귀: 클레임이 빠진 평문 토큰(alg=none)도 500 이 아니다")
        void verify_plainJwt() {

            assertThatThrownBy(() -> appleIdTokenProvider.verify("eyJhbGciOiJub25lIn0.e30."))
                    .isInstanceOf(InvalidIdTokenException.class);
        }

        @Test
        @DisplayName("JWT 형식이 아니면 거부한다")
        void verify_notAJwt() {

            assertThatThrownBy(() -> appleIdTokenProvider.verify("not-a-jwt"))
                    .isInstanceOf(InvalidIdTokenException.class);
        }

        @Test
        @DisplayName("JWKS 가 죽으면 토큰 잘못이 아니라 일시 장애로 구분한다")
        void verify_jwksOutage() throws Exception {

            JWKSource<SecurityContext> brokenSource = (selector, context) -> {
                throw new RemoteKeySourceException("jwks unreachable", null);
            };

            AppleIdTokenProvider provider = new AppleIdTokenProvider(
                    new AppleIdTokenConfig().appleJwtProcessor(brokenSource, List.of(AUDIENCE)));

            String token = sign(validClaims().build());

            assertThatThrownBy(() -> provider.verify(token))
                    .isInstanceOf(IdTokenVerifyFailedException.class);
        }
    }
}
