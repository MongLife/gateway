package com.monglife.discovery.app.common.feedback.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * monglife-mongs 의 CreateFeedbackRequestDto 와 필드가 같다 (앱이 그대로 보낸다).
 * 앱 패키지·버전은 본문이 아니라 토큰에서 채운다.
 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateFeedbackRequestDto {

    @NotBlank
    @Size(max = 255)
    private String deviceName;

    @NotBlank
    @Size(max = 255)
    private String title;

    @NotNull
    @Size(max = 5000)
    private String content;

    @Builder
    public CreateFeedbackRequestDto(String deviceName, String title, String content) {
        this.deviceName = deviceName;
        this.title = title;
        this.content = content;
    }
}
