package com.monglife.discovery.domain.account.entity;

import com.monglife.core.enums.role.RoleCode;
import com.monglife.module.common.jpa.entity.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(
        name = "monglife_account",
        indexes = {
                // Apple 로그인이 매 요청 social_account_id 로 조회한다.
                // columnList 는 물리명이 아니라 **논리 컬럼명**(= @Column 에 name 이 없으면 필드명)으로
                // 매칭된다. 스네이크로 쓰면 "column not found" 로 기동이 깨진다.
                // 물리명은 PhysicalNamingStrategy 가 뒤에 social_account_id 로 바꾼다.
                // UNIQUE 는 기존 데이터 중복을 확인한 뒤 후속으로 승격한다.
                // stg/prd 는 hbm2ddl 이 none 이라 수동 DDL 이 필요하다
                @Index(name = "idx_monglife_account_social_account_id", columnList = "socialAccountId")
        }
)
public class AccountEntity extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long accountId;

    @Column
    private String socialAccountId;

    @Column(updatable = false, nullable = false)
    private String email;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Boolean isDeleted = false;

    @Column(nullable = false)
    private String role = RoleCode.NORMAL.getRole();

    @Builder
    public AccountEntity(String socialAccountId, String email, String name, String role) {
        this.socialAccountId = socialAccountId;
        this.email = email;
        this.name = name;
        this.isDeleted = false;
        this.role = role;
    }

    public void updateSocialAccountId(String socialAccountId) {
        this.socialAccountId = socialAccountId;
    }
}
