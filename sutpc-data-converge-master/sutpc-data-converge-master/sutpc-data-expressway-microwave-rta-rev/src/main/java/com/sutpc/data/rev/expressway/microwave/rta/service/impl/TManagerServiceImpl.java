package com.sutpc.data.rev.expressway.microwave.rta.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TManager;
import com.sutpc.data.rev.expressway.microwave.rta.mapper.TManagerMapper;
import com.sutpc.data.rev.expressway.microwave.rta.service.TManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *  .
 * @Auth smilesnake minyikun
 * @Create 2019/8/15 10:57
 */
@Service
public class TManagerServiceImpl implements TManagerService {

  @Autowired
  private TManagerMapper mapper;

  @Transactional(readOnly = true)
  @Override
  public Page<TManager> findAll(Integer pagenum, Integer pagesize) {
    PageHelper.startPage(pagenum, pagesize);
    Page<TManager> page = mapper.findAll();
    return page;
  }
}
