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
public class AdminAppVersionCreateRequestDto {

    @NotBlank
    private String appPackageName;

    @NotBlank @Pattern(regexp = "\\d+(\\.\\d+)*")
    private String buildVersion;

    @NotNull
    private Boolean mustUpdate;

    @Builder
    public AdminAppVersionCreateRequestDto(String appPackageName, String buildVersion, Boolean mustUpdate) {
        this.appPackageName = appPackageName;
        this.buildVersion = buildVersion;
        this.mustUpdate = mustUpdate;
    }
}
