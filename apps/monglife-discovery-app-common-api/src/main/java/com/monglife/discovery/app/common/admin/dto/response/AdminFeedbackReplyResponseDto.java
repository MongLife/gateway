package com.monglife.discovery.app.common.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AdminFeedbackReplyResponseDto {

    private final String content;

    private final String sentTo;

    // 기본 ObjectMapper 가 날짜를 배열로 내보내므로 ISO 문자열로 고정한다
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd\'T\'HH:mm:ss")
    private final LocalDateTime createdAt;

    @Builder
    public AdminFeedbackReplyResponseDto(String content, String sentTo, LocalDateTime createdAt) {
        this.content = content;
        this.sentTo = sentTo;
        this.createdAt = createdAt;
    }
}
