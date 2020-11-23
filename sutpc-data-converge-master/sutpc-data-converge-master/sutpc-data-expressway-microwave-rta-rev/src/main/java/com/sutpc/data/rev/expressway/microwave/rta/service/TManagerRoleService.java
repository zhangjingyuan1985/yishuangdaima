package com.sutpc.data.rev.expressway.microwave.rta.service;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TManagerRole;

public interface TManagerRoleService {
  Page<TManagerRole> findAll(Integer pagenum, Integer pagesize);
}
