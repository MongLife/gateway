package com.monglife.discovery.domain.device.vo;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/** 관리자 기기 목록 조회 조건. null 은 무시 */
@Getter
public class DeviceSearchVo {

    /** deviceId / deviceName like, 또는 queryAccountIds 에 포함 */
    private final String query;

    /** query 가 계정 이메일·이름과 매칭된 계정 ID 들 (app 계층에서 채움) */
    private final List<Long> queryAccountIds;

    private final Boolean unmappedOnly;

    /** true: FCM 토큰 있음, false: 없음 */
    private final Boolean hasFcmToken;

    /** true: 알림 가능 기기만 (fcm + account 연결) */
    private final Boolean notifiableOnly;

    private final String deviceName;

    private final Long accountId;

    private final int page;

    private final int size;

    private final boolean sortDesc;

    @Builder
    public DeviceSearchVo(String query, List<Long> queryAccountIds, Boolean unmappedOnly, Boolean hasFcmToken, Boolean notifiableOnly, String deviceName, Long accountId, int page, int size, boolean sortDesc) {
        this.query = query;
        this.queryAccountIds = queryAccountIds;
        this.unmappedOnly = unmappedOnly;
        this.hasFcmToken = hasFcmToken;
        this.notifiableOnly = notifiableOnly;
        this.deviceName = deviceName;
        this.accountId = accountId;
        this.page = page;
        this.size = size;
        this.sortDesc = sortDesc;
    }
}
