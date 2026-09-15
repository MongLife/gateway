package com.monglife.discovery.app.common.admin.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class AdminLoginStatResponseDto {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private final LocalDate date;

    private final long loginCount;

    private final long uniqueAccounts;

    @Builder
    public AdminLoginStatResponseDto(LocalDate date, long loginCount, long uniqueAccounts) {
        this.date = date;
        this.loginCount = loginCount;
        this.uniqueAccounts = uniqueAccounts;
    }
}
