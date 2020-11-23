package com.sutpc.data.rev.expressway.microwave.rta.service;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TSeQueueHis;

public interface TSeQueueHisService {
  Page<TSeQueueHis> findAll(Integer pagenum, Integer pagesize);
}
