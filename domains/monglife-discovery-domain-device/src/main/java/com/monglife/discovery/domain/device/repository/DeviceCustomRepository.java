package com.monglife.discovery.domain.device.repository;

import com.monglife.discovery.domain.device.entity.DeviceEntity;
import com.monglife.discovery.domain.device.vo.DeviceSearchVo;

import java.util.List;

public interface DeviceCustomRepository {

    List<DeviceEntity> findPage(DeviceSearchVo cond);

    long countPage(DeviceSearchVo cond);

    List<String> findDistinctDeviceNames();
}
