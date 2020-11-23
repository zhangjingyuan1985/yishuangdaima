package com.sutpc.data.rev.expressway.microwave.rta.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TSeQueueHis;
import com.sutpc.data.rev.expressway.microwave.rta.mapper.TSeQueueHisMapper;
import com.sutpc.data.rev.expressway.microwave.rta.service.TSeQueueHisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *  .
 * @Auth smilesnake minyikun
 * @Create 2019/8/15 11:01
 */
@Service
public class TSeQueueHisServiceImpl implements TSeQueueHisService {
  @Autowired
  private TSeQueueHisMapper mapper;

  @Transactional(readOnly = true)
  @Override
  public Page<TSeQueueHis> findAll(Integer pagenum, Integer pagesize) {
    PageHelper.startPage(pagenum, pagesize);
    Page<TSeQueueHis> page = mapper.findAll();
    return page;
  }
}
