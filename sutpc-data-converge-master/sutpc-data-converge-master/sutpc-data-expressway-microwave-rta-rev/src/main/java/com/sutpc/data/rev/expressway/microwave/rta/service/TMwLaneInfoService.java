package com.sutpc.data.rev.expressway.microwave.rta.service;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TMwLaneInfo;

public interface TMwLaneInfoService {
  Page<TMwLaneInfo> findAll(Integer pagenum, Integer pagesize);
}
