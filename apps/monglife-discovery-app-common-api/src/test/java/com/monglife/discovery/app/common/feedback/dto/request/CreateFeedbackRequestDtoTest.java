package com.monglife.discovery.app.common.feedback.dto.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CreateFeedbackRequestDto — 앱(mongs 와 동일 페이로드) JSON 과 1:1")
class CreateFeedbackRequestDtoTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("deviceName / title / content 를 읽고, 모르는 필드는 무시한다")
    void deserialize() throws Exception {
        String json = """
                {"deviceName":"Galaxy S24","title":"앱이 꺼져요","content":"실행 직후 종료됩니다","extra":"ignored"}
                """;

        CreateFeedbackRequestDto dto = objectMapper.readValue(json, CreateFeedbackRequestDto.class);

        assertThat(dto.getDeviceName()).isEqualTo("Galaxy S24");
        assertThat(dto.getTitle()).isEqualTo("앱이 꺼져요");
        assertThat(dto.getContent()).isEqualTo("실행 직후 종료됩니다");
    }
}
