package com.sutpc.data.rev.expressway.microwave.rta.service;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TManager;

public interface TManagerService {
  Page<TManager> findAll(Integer pagenum, Integer pagesize);
}
