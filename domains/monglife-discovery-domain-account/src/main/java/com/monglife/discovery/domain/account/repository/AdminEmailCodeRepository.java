package com.monglife.discovery.domain.account.repository;

import com.monglife.discovery.domain.account.entity.AdminEmailCodeEntity;
import org.springframework.data.repository.CrudRepository;

public interface AdminEmailCodeRepository extends CrudRepository<AdminEmailCodeEntity, String> {
}
