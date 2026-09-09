package com.monglife.discovery.domain.account.service;

import com.monglife.discovery.domain.account.entity.AccountEntity;
import com.monglife.discovery.domain.account.exception.AlreadyExistsAccountException;
import com.monglife.discovery.domain.account.exception.NotExistsAccountException;
import com.monglife.discovery.domain.account.repository.AccountRepository;
import com.monglife.discovery.domain.account.vo.AccountVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final AccountRepository accountRepository;

    /**
     * 계정 생성
     * @param accountVo 계정 생성 정보 Vo
     */
    @Transactional
    public void createAccount(AccountVo accountVo) {

        accountRepository.findByEmail(accountVo.getEmail())
                .ifPresent(accountEntity -> { throw new AlreadyExistsAccountException(); });

        AccountEntity accountEntity = AccountEntity.builder()
                .email(accountVo.getEmail())
                .name(accountVo.getName())
                .socialAccountId(accountVo.getSocialAccountId())
                .role(accountVo.getRole())
                .build();

        accountRepository.save(accountEntity);
    }

    /**
     * 소셜 계정 생성
     * createAccount 와 달리 socialAccountId 를 중복 검사 기준으로 삼는다.
     * Apple 은 이메일을 숨길 수 있어 이메일을 계정 키로 쓸 수 없기 때문이다.
     * social_account_id 에 UNIQUE 제약이 없어 이 검사에 의존한다 — 동시 요청은 막지 못한다.
     * @param accountVo 계정 생성 정보 Vo
     */
    @Transactional
    public void createSocialAccount(AccountVo accountVo) {

        accountRepository.findBySocialAccountId(accountVo.getSocialAccountId())
                .ifPresent(accountEntity -> { throw new AlreadyExistsAccountException(); });

        // email 은 NOT NULL 이고 findByEmail 이 fetchOne 이라 기존 불변식도 함께 지킨다
        accountRepository.findByEmail(accountVo.getEmail())
                .ifPresent(accountEntity -> { throw new AlreadyExistsAccountException(); });

        AccountEntity accountEntity = AccountEntity.builder()
                .email(accountVo.getEmail())
                .name(accountVo.getName())
                .socialAccountId(accountVo.getSocialAccountId())
                .role(accountVo.getRole())
                .build();

        accountRepository.save(accountEntity);
    }

    /**
     * 이메일 기준 계정 정보 조회
     * @param email 이메일
     * @return 계정 정보 Vo
     */
    @Transactional(readOnly = true)
    public AccountVo getAccount(String email) {

        AccountEntity accountEntity = accountRepository.findByEmail(email)
                .orElseThrow(NotExistsAccountException::new);

        return AccountVo.builder()
                .accountId(accountEntity.getAccountId())
                .email(accountEntity.getEmail())
                .name(accountEntity.getName())
                .socialAccountId(accountEntity.getSocialAccountId())
                .role(accountEntity.getRole())
                .build();
    }

    /**
     * 계정 ID 기준 계정 정보 조회
     * @param accountId 계정 ID
     * @return 계정 정보 Vo
     */
    @Transactional(readOnly = true)
    public AccountVo getAccount(Long accountId) {

        AccountEntity accountEntity = accountRepository.findByAccountId(accountId)
                .orElseThrow(NotExistsAccountException::new);

        return AccountVo.builder()
                .email(accountEntity.getEmail())
                .name(accountEntity.getName())
                .socialAccountId(accountEntity.getSocialAccountId())
                .role(accountEntity.getRole())
                .build();
    }

    /**
     * 소셜 계정 ID 기준 계정 정보 조회
     * getAccount(String) 은 이메일 오버로드가 이미 점유하고 있어 이름을 분리했다.
     * @param socialAccountId 소셜 계정 ID
     * @return 계정 정보 Vo
     */
    @Transactional(readOnly = true)
    public AccountVo getAccountBySocialAccountId(String socialAccountId) {

        AccountEntity accountEntity = accountRepository.findBySocialAccountId(socialAccountId)
                .orElseThrow(() -> new NotExistsAccountException(socialAccountId));

        return AccountVo.builder()
                // 세션 발급에 필요하다. getAccount(Long) 이 이 필드를 빠뜨리고 있으니 따라하지 않는다
                .accountId(accountEntity.getAccountId())
                .email(accountEntity.getEmail())
                .name(accountEntity.getName())
                .socialAccountId(accountEntity.getSocialAccountId())
                .role(accountEntity.getRole())
                .build();
    }

    /**
     * 구글 계정 ID 업데이트
     * @param email 이메일
     * @param socialAccountId 구글 계정 ID
     */
    @Transactional
    public void updateSocialAccountId(String email, String socialAccountId) {

        AccountEntity accountEntity = accountRepository.findByEmail(email)
                .orElseThrow(NotExistsAccountException::new);

        accountEntity.updateSocialAccountId(socialAccountId);
    }
}
