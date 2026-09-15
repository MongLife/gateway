package com.monglife.discovery.app.common.auth.dto.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * iOS 가 실제로 보내는 JSON 을 그대로 박아두고 역직렬화를 검증한다.
 * 로깅 모듈이 등록하는 ObjectMapper 때문에 운영에서 FAIL_ON_UNKNOWN_PROPERTIES 가
 * 켜진 채로 동작하므로, 필드가 하나라도 어긋나면 500 이 난다.
 */
@DisplayName("apple 요청 DTO 역직렬화 계약")
class AppleRequestDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("AppleLoginRequestDto - 클라이언트 JSON 의 전 필드가 채워진다")
    void loginRequest() throws Exception {

        String json = """
                {
                  "socialAccountId": "001234.a1b2c3d4e5f6.1234",
                  "identityToken": "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxIn0.sig",
                  "deviceId": "9F3A2B10-4C5D-6E7F-8A9B-0C1D2E3F4A5B",
                  "appPackageName": "com.mongs.wear",
                  "deviceName": "Apple Watch",
                  "buildVersion": "2.2.1"
                }
                """;

        AppleLoginRequestDto dto = objectMapper.readValue(json, AppleLoginRequestDto.class);

        assertThat(dto.getSocialAccountId()).isEqualTo("001234.a1b2c3d4e5f6.1234");
        assertThat(dto.getIdentityToken()).isEqualTo("eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxIn0.sig");
        assertThat(dto.getDeviceId()).isEqualTo("9F3A2B10-4C5D-6E7F-8A9B-0C1D2E3F4A5B");
        assertThat(dto.getAppPackageName()).isEqualTo("com.mongs.wear");
        assertThat(dto.getDeviceName()).isEqualTo("Apple Watch");
        assertThat(dto.getBuildVersion()).isEqualTo("2.2.1");
    }

    @Test
    @DisplayName("AppleJoinRequestDto - 클라이언트 JSON 의 전 필드가 채워진다")
    void joinRequest() throws Exception {

        String json = """
                {
                  "socialAccountId": "001234.a1b2c3d4e5f6.1234",
                  "identityToken": "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxIn0.sig",
                  "email": "abc123@privaterelay.appleid.com",
                  "name": "홍 길동"
                }
                """;

        AppleJoinRequestDto dto = objectMapper.readValue(json, AppleJoinRequestDto.class);

        assertThat(dto.getSocialAccountId()).isEqualTo("001234.a1b2c3d4e5f6.1234");
        assertThat(dto.getIdentityToken()).isEqualTo("eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxIn0.sig");
        assertThat(dto.getEmail()).isEqualTo("abc123@privaterelay.appleid.com");
        assertThat(dto.getName()).isEqualTo("홍 길동");
    }

    @Test
    @DisplayName("AppleJoinRequestDto - email/name 키가 생략돼도 역직렬화되고 두 필드는 null 이다")
    void joinRequestWithoutOptionalFields() throws Exception {

        // iOS 는 값이 nil 이면 키 자체를 빼고 보낸다. 이메일을 숨기거나 재로그인한 경우다
        String json = """
                {
                  "socialAccountId": "001234.a1b2c3d4e5f6.1234",
                  "identityToken": "eyJhbGciOiJSUzI1NiJ9.eyJzdWIiOiIxIn0.sig"
                }
                """;

        AppleJoinRequestDto dto = objectMapper.readValue(json, AppleJoinRequestDto.class);

        assertThat(dto.getEmail()).isNull();
        assertThat(dto.getName()).isNull();
        assertThat(dto.getSocialAccountId()).isEqualTo("001234.a1b2c3d4e5f6.1234");
    }

    @Test
    @DisplayName("AppleJoinRequestDto - 모르는 필드가 와도 500 이 나지 않는다")
    void joinIgnoresUnknownFields() {

        String json = """
                {
                  "socialAccountId": "001234.a1b2c3d4e5f6.1234",
                  "identityToken": "token",
                  "somethingNewFromClient": "whatever"
                }
                """;

        assertThatCode(() -> objectMapper.readValue(json, AppleJoinRequestDto.class))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("AppleLoginRequestDto - 모르는 필드가 와도 500 이 나지 않는다")
    void loginIgnoresUnknownFields() {

        // 클라이언트가 나중에 email 을 실어 보내도 안전해야 한다
        String json = """
                {
                  "socialAccountId": "001234.a1b2c3d4e5f6.1234",
                  "identityToken": "token",
                  "deviceId": "9F3A2B10-4C5D-6E7F-8A9B-0C1D2E3F4A5B",
                  "appPackageName": "com.mongs.wear",
                  "deviceName": "Apple Watch",
                  "buildVersion": "2.2.1",
                  "email": "abc123@privaterelay.appleid.com"
                }
                """;

        assertThatCode(() -> objectMapper.readValue(json, AppleLoginRequestDto.class))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("빌더가 전 필드를 받는다")
    void builderCoversAllFields() {

        AppleLoginRequestDto dto = AppleLoginRequestDto.builder()
                .socialAccountId("001234.a1b2c3d4e5f6.1234")
                .identityToken("token")
                .deviceId("9F3A2B10-4C5D-6E7F-8A9B-0C1D2E3F4A5B")
                .appPackageName("com.mongs.wear")
                .deviceName("Apple Watch")
                .buildVersion("2.2.1")
                .build();

        assertThat(dto.getDeviceName()).isEqualTo("Apple Watch");
        assertThat(dto.getIdentityToken()).isEqualTo("token");
    }
}
