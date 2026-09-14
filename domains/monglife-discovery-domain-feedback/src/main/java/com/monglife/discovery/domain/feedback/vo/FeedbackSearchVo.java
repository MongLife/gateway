package com.monglife.discovery.domain.feedback.vo;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/** 관리자 오류 신고 목록 조건. null 은 무시 */
@Getter
public class FeedbackSearchVo {

    /** title like, 또는 queryAccountIds 에 포함 */
    private final String query;

    /** query 가 계정 이메일·이름과 매칭된 계정 ID 들 (app 계층에서 채움) */
    private final List<Long> queryAccountIds;

    private final String status;

    private final String deviceName;

    private final String appPackageName;

    private final String buildVersion;

    private final int page;

    private final int size;

    /** feedbackId | createdAt */
    private final String sortKey;

    private final boolean sortDesc;

    @Builder
    public FeedbackSearchVo(String query, List<Long> queryAccountIds, String status, String deviceName, String appPackageName, String buildVersion, int page, int size, String sortKey, boolean sortDesc) {
        this.query = query;
        this.queryAccountIds = queryAccountIds;
        this.status = status;
        this.deviceName = deviceName;
        this.appPackageName = appPackageName;
        this.buildVersion = buildVersion;
        this.page = page;
        this.size = size;
        this.sortKey = sortKey;
        this.sortDesc = sortDesc;
    }
}
