package com.monglife.discovery.domain.device.service;

import com.monglife.discovery.domain.device.entity.DeviceEntity;
import com.monglife.discovery.domain.device.exception.NotExistsDeviceException;
import com.monglife.discovery.domain.device.repository.DeviceRepository;
import com.monglife.discovery.domain.device.vo.DeviceSearchVo;
import com.monglife.discovery.domain.device.vo.DeviceVo;
import com.monglife.discovery.domain.device.vo.PageVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    static DeviceVo toVo(DeviceEntity e) {
        return DeviceVo.builder()
                .deviceId(e.getDeviceId())
                .deviceName(e.getDeviceName())
                .fcmToken(e.getFcmToken())
                .accountId(e.getAccountId())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    /**
     * 기기 정보 등록
     * @param deviceId 기기 ID
     * @param deviceName 기기명
     * @param fcmToken FCM 토큰
     */
    @Transactional
    public DeviceVo createDevice(String deviceId, String deviceName, String fcmToken) {

        // 기존 기기 정보가 존재하지 않는 경우 등록
        DeviceEntity deviceEntity = deviceRepository.findByDeviceId(deviceId)
                .orElseGet(() -> deviceRepository.save(DeviceEntity.builder()
                        .deviceId(deviceId)
                        .deviceName(deviceName)
                        .fcmToken(fcmToken)
                        .build()));

        return toVo(deviceEntity);
    }

    /**
     * 기기 정보 목록 조회
     * @param accountId 계정 ID
     * @return 기기 정보 Vo 목록
     */
    @Transactional(readOnly = true)
    public List<DeviceVo> getDevices(Long accountId) {
        return deviceRepository.findByAccountId(accountId).stream().map(DeviceService::toVo).toList();
    }

    /**
     * FCM 토큰 업데이트
     * @param deviceId 기기 ID
     * @param fcmToken FCM 토큰
     */
    @Transactional
    public void updateDevice(String deviceId, String fcmToken) {

        DeviceEntity deviceEntity = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new NotExistsDeviceException(deviceId));

        deviceEntity.setFcmToken(fcmToken);
    }

    /**
     * 계정 ID 연결
     * @param deviceId 기기 ID
     * @param accountId 계정 ID
     */
    @Transactional
    public void connectAccountId(String deviceId, Long accountId) {

        DeviceEntity deviceEntity = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new NotExistsDeviceException(deviceId));

        deviceEntity.connectAccount(accountId);
    }

    /**
     * 계정 ID 연결 해제
     * @param deviceId 기기 ID
     * @param accountId 계정 ID
     */
    @Transactional
    public void disconnectAccountId(String deviceId, Long accountId) {

        DeviceEntity deviceEntity = deviceRepository.findByAccountIdAndDeviceId(accountId, deviceId)
                .orElseThrow(() -> new NotExistsDeviceException(deviceId));

        deviceEntity.disconnectAccount();
    }

    // ----- 관리자 -----

    @Transactional(readOnly = true)
    public DeviceVo getDevice(String deviceId) {
        return toVo(deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new NotExistsDeviceException(deviceId)));
    }

    /** 있는 것만 돌려준다 (없는 ID 는 건너뜀 — 예외로 트랜잭션을 깨지 않는다) */
    @Transactional(readOnly = true)
    public List<DeviceVo> getDevicesByIds(Collection<String> deviceIds) {
        if (deviceIds.isEmpty()) return List.of();
        return deviceRepository.findByDeviceIdIn(deviceIds).stream().map(DeviceService::toVo).toList();
    }

    @Transactional(readOnly = true)
    public PageVo<DeviceVo> getDevices(DeviceSearchVo cond) {
        return PageVo.<DeviceVo>builder()
                .items(deviceRepository.findPage(cond).stream().map(DeviceService::toVo).toList())
                .page(cond.getPage())
                .size(cond.getSize())
                .total(deviceRepository.countPage(cond))
                .build();
    }

    /** 관리자 해제: 계정 일치 검사 없이 기기 ID 로 해제 */
    @Transactional
    public DeviceVo disconnectAccountId(String deviceId) {
        DeviceEntity deviceEntity = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new NotExistsDeviceException(deviceId));
        deviceEntity.disconnectAccount();
        return toVo(deviceEntity);
    }

    /** 계정의 모든 기기 연결 해제 (탈퇴 처리) */
    @Transactional
    public void disconnectAll(Long accountId) {
        deviceRepository.findByAccountId(accountId).forEach(DeviceEntity::disconnectAccount);
    }

    @Transactional
    public void deleteDevice(String deviceId) {
        DeviceEntity deviceEntity = deviceRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new NotExistsDeviceException(deviceId));
        deviceRepository.delete(deviceEntity);
    }

    @Transactional(readOnly = true)
    public List<String> getDeviceNames() {
        return deviceRepository.findDistinctDeviceNames();
    }
}
