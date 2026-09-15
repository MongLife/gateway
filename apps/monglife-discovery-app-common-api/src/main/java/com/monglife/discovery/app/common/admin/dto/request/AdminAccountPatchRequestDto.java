package com.monglife.discovery.app.common.admin.dto.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/** null 인 필드는 건드리지 않는다 */
@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class AdminAccountPatchRequestDto {

    @Size(max = 100)
    private String name;

    @Pattern(regexp = "ADMIN|NORMAL")
    private String role;

    private Boolean isDeleted;

    @Builder
    public AdminAccountPatchRequestDto(String name, String role, Boolean isDeleted) {
        this.name = name;
        this.role = role;
        this.isDeleted = isDeleted;
    }
}
