package com.monglife.discovery.domain.device.service;

import com.monglife.discovery.domain.device.entity.AppVersionEntity;
import com.monglife.discovery.domain.device.exception.AlreadyExistsAppVersionException;
import com.monglife.discovery.domain.device.exception.NotExistsAppVersionException;
import com.monglife.discovery.domain.device.repository.AppVersionRepository;
import com.monglife.discovery.domain.device.vo.AppVersionVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AppVersionService {

    private final AppVersionRepository appVersionRepository;

    static AppVersionVo toVo(AppVersionEntity e) {
        return AppVersionVo.builder()
                .appVersionId(e.getAppVersionId())
                .appPackageName(e.getAppPackageName())
                .buildVersion(e.getBuildVersion())
                .mustUpdate(e.getMustUpdate())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }

    /**
     * 앱 버전 정보 조회
     * @param appPackageName 앱 패키지 명
     * @param buildVersion 빌드 버전
     * @return 앱 버전 정보 Vo
     */
    @Transactional(readOnly = true)
    public AppVersionVo getAppVersion(String appPackageName, String buildVersion) {

        AppVersionEntity appVersionEntity = appVersionRepository.findByAppPackageNameAndBuildVersion(appPackageName, buildVersion)
                .orElseThrow(() -> new NotExistsAppVersionException(appPackageName, buildVersion));

        return toVo(appVersionEntity);
    }

    // ----- 관리자 -----

    @Transactional(readOnly = true)
    public List<AppVersionVo> getAppVersions() {
        return appVersionRepository.findAllByOrderByAppPackageNameAscAppVersionIdDesc().stream()
                .map(AppVersionService::toVo).toList();
    }

    @Transactional
    public AppVersionVo createAppVersion(String appPackageName, String buildVersion, Boolean mustUpdate) {
        appVersionRepository.findByAppPackageNameAndBuildVersion(appPackageName, buildVersion)
                .ifPresent(e -> { throw new AlreadyExistsAppVersionException(appPackageName, buildVersion); });
        return toVo(appVersionRepository.save(AppVersionEntity.builder()
                .appPackageName(appPackageName)
                .buildVersion(buildVersion)
                .mustUpdate(mustUpdate)
                .build()));
    }

    @Transactional
    public AppVersionVo updateMustUpdate(Long appVersionId, Boolean mustUpdate) {
        AppVersionEntity e = appVersionRepository.findById(appVersionId)
                .orElseThrow(() -> new NotExistsAppVersionException(String.valueOf(appVersionId), "-"));
        e.updateMustUpdate(mustUpdate);
        return toVo(e);
    }

    @Transactional
    public void deleteAppVersion(Long appVersionId) {
        AppVersionEntity e = appVersionRepository.findById(appVersionId)
                .orElseThrow(() -> new NotExistsAppVersionException(String.valueOf(appVersionId), "-"));
        appVersionRepository.delete(e);
    }

    @Transactional(readOnly = true)
    public List<String> getPackageNames() {
        return appVersionRepository.findAllByOrderByAppPackageNameAscAppVersionIdDesc().stream()
                .map(AppVersionEntity::getAppPackageName).distinct().sorted().toList();
    }

    @Transactional(readOnly = true)
    public List<String> getBuildVersions() {
        return appVersionRepository.findAllByOrderByAppPackageNameAscAppVersionIdDesc().stream()
                .map(AppVersionEntity::getBuildVersion).distinct().toList();
    }
}
