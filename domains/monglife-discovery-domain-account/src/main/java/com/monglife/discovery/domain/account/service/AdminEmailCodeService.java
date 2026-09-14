package com.monglife.discovery.domain.account.service;

import com.monglife.discovery.domain.account.entity.AdminEmailCodeEntity;
import com.monglife.discovery.domain.account.repository.AdminEmailCodeRepository;
import com.monglife.discovery.domain.account.vo.AdminEmailCodeVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminEmailCodeService {

    private final AdminEmailCodeRepository adminEmailCodeRepository;

    @Transactional
    public void saveCode(AdminEmailCodeVo vo) {
        adminEmailCodeRepository.save(AdminEmailCodeEntity.builder()
                .email(vo.getEmail())
                .code(vo.getCode())
                .issuedAt(vo.getIssuedAt())
                .expiration(vo.getExpiration())
                .build());
    }

    @Transactional(readOnly = true)
    public Optional<AdminEmailCodeVo> getCode(String email) {
        return adminEmailCodeRepository.findById(email)
                .map(e -> AdminEmailCodeVo.builder()
                        .email(e.getEmail())
                        .code(e.getCode())
                        .issuedAt(e.getIssuedAt())
                        .expiration(e.getExpiration())
                        .build());
    }

    @Transactional
    public void deleteCode(String email) {
        adminEmailCodeRepository.deleteById(email);
    }
}
