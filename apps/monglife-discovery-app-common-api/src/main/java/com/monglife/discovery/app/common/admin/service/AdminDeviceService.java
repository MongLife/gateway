package com.monglife.discovery.app.common.admin.service;

import com.monglife.discovery.app.common.admin.dto.response.AdminDeviceResponseDto;
import com.monglife.discovery.app.common.admin.util.AdminPage;
import com.monglife.discovery.domain.account.service.AccountService;
import com.monglife.discovery.domain.account.service.TokenService;
import com.monglife.discovery.domain.account.vo.AccountVo;
import com.monglife.discovery.domain.device.service.DeviceService;
import com.monglife.discovery.domain.device.vo.DeviceSearchVo;
import com.monglife.discovery.domain.device.vo.DeviceVo;
import com.monglife.discovery.domain.device.vo.PageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class AdminDeviceService {

    private final DeviceService deviceService;
    private final AccountService accountService;
    private final TokenService tokenService;
    private final AdminAccountService adminAccountService;

    @Transactional(readOnly = true)
    public AdminPage<AdminDeviceResponseDto> getDevices(String query, Boolean unmappedOnly, String fcm, String deviceName, boolean sortDesc, int page, int size) {
        Boolean hasFcm = fcm == null ? null : "REGISTERED".equalsIgnoreCase(fcm);
        return toPage(deviceService.getDevices(DeviceSearchVo.builder()
                .query(query)
                .queryAccountIds(adminAccountService.accountIdsByQuery(query))
                .unmappedOnly(unmappedOnly)
                .hasFcmToken(hasFcm)
                .deviceName(deviceName)
                .page(page)
                .size(size)
                .sortDesc(sortDesc)
                .build()));
    }

    /** 푸시 알림 가능 기기 (FCM 토큰 + 계정 연결) */
    @Transactional(readOnly = true)
    public AdminPage<AdminDeviceResponseDto> getNotifiableDevices(String query, Long accountId, String deviceName, int page, int size) {
        return toPage(deviceService.getDevices(DeviceSearchVo.builder()
                .query(query)
                .queryAccountIds(adminAccountService.accountIdsByQuery(query))
                .notifiableOnly(true)
                .accountId(accountId)
                .deviceName(deviceName)
                .page(page)
                .size(size)
                .sortDesc(true)
                .build()));
    }

    private AdminPage<AdminDeviceResponseDto> toPage(PageVo<DeviceVo> result) {
        List<Long> ids = result.getItems().stream().map(DeviceVo::getAccountId).filter(Objects::nonNull).distinct().toList();
        Map<Long, AccountVo> accounts = adminAccountService.accountMap(ids);
        return new AdminPage<>(result.getItems().stream().map(d -> AdminMapper.device(d, accounts)).toList(),
                result.getPage(), result.getSize(), result.getTotal());
    }

    @Transactional
    public AdminDeviceResponseDto connect(String deviceId, Long accountId) {
        // 탈퇴 계정에는 붙이지 않는다 (findByAccountId 는 isDeleted=false 만 찾는다)
        AccountVo account = accountService.getAccount(accountId);
        deviceService.connectAccountId(deviceId, accountId);
        return AdminMapper.device(deviceService.getDevice(deviceId), Map.of(accountId, account));
    }

    @Transactional
    public AdminDeviceResponseDto disconnect(String deviceId) {
        DeviceVo device = deviceService.disconnectAccountId(deviceId);
        return AdminMapper.device(device, Map.of());
    }

    /** 기기와 그 기기의 세션(토큰)을 함께 지운다 */
    @Transactional
    public void delete(String deviceId) {
        deviceService.deleteDevice(deviceId);
        tokenService.deleteTokensByDeviceId(deviceId);
    }
}
