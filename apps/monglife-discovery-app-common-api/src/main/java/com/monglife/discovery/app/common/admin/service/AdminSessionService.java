package com.monglife.discovery.app.common.admin.service;

import com.monglife.discovery.app.common.admin.dto.response.AdminSessionResponseDto;
import com.monglife.discovery.app.common.admin.util.AdminPage;
import com.monglife.discovery.domain.account.service.TokenService;
import com.monglife.discovery.domain.account.vo.AccountVo;
import com.monglife.discovery.domain.account.vo.TokenVo;
import com.monglife.discovery.domain.device.service.DeviceService;
import com.monglife.discovery.domain.device.vo.DeviceVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 로그인 현황 = Redis 의 유효 토큰. Redis 는 조건 검색이 없어 전부 읽어 메모리에서 거른다.
 * 토큰 수는 활성 기기 수 수준이라 감당된다.
 */
@Service
@RequiredArgsConstructor
public class AdminSessionService {

    private final TokenService tokenService;
    private final DeviceService deviceService;
    private final AdminAccountService adminAccountService;

    @Transactional(readOnly = true)
    public AdminPage<AdminSessionResponseDto> getSessions(Long accountId, String deviceName, String appPackageName, String buildVersion, boolean sortDesc, int page, int size) {

        List<TokenVo> tokens = accountId == null ? tokenService.getTokens() : tokenService.getTokensByAccountId(accountId);

        // 관리자 웹 토큰(admin-web)처럼 기기 테이블에 없는 ID 도 있으니 있는 것만 조인한다
        Map<String, DeviceVo> devices = deviceService.getDevicesByIds(tokens.stream().map(TokenVo::getDeviceId).distinct().toList()).stream()
                .collect(Collectors.toMap(DeviceVo::getDeviceId, Function.identity(), (a, b) -> a));

        List<TokenVo> filtered = tokens.stream()
                .filter(t -> deviceName == null || (devices.get(t.getDeviceId()) != null && deviceName.equals(devices.get(t.getDeviceId()).getDeviceName())))
                .filter(t -> appPackageName == null || appPackageName.equals(t.getAppPackageName()))
                .filter(t -> buildVersion == null || buildVersion.equals(t.getBuildVersion()))
                .sorted(sortDesc
                        ? Comparator.comparing(TokenVo::getCreatedAt, Comparator.nullsLast(Comparator.<LocalDateTime>naturalOrder())).reversed()
                        : Comparator.comparing(TokenVo::getCreatedAt, Comparator.nullsLast(Comparator.<LocalDateTime>naturalOrder())))
                .toList();

        List<TokenVo> slice = filtered.stream().skip((long) page * size).limit(size).toList();
        Map<Long, AccountVo> accounts = adminAccountService.accountMap(slice.stream().map(TokenVo::getAccountId).distinct().toList());

        return new AdminPage<>(slice.stream().map(t -> AdminMapper.session(t, accounts, devices)).toList(), page, size, filtered.size());
    }

    @Transactional
    public void revoke(String refreshToken) {
        tokenService.deleteToken(refreshToken);
    }

    @Transactional
    public void revokeAll(Long accountId) {
        tokenService.deleteTokensByAccountId(accountId);
    }
}
