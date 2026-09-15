package com.monglife.discovery.app.common.adminauth.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminEmailCodeRequestDto {

    @NotBlank
    @Email
    private String email;

    @Builder
    public AdminEmailCodeRequestDto(String email) {
        this.email = email;
    }
}
