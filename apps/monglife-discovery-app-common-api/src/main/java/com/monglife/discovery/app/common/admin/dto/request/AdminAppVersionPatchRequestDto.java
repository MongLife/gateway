package com.monglife.discovery.app.common.admin.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminAppVersionPatchRequestDto {

    @NotNull
    private Boolean mustUpdate;

    @Builder
    public AdminAppVersionPatchRequestDto(Boolean mustUpdate) {
        this.mustUpdate = mustUpdate;
    }
}
