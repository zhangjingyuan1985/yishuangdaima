package com.sutpc.data.rev.expressway.microwave.rta.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TMwLaneInfo;
import com.sutpc.data.rev.expressway.microwave.rta.mapper.TMwLaneInfoMapper;
import com.sutpc.data.rev.expressway.microwave.rta.service.TMwLaneInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * .
 *
 * @Auth smilesnake minyikun
 * @Create 2019/8/15 10:58
 */
@Service
public class TMwLaneInfoServiceImpl implements TMwLaneInfoService {

  @Autowired
  private TMwLaneInfoMapper mapper;

  @Transactional(readOnly = true)
  @Override
  public Page<TMwLaneInfo> findAll(Integer pagenum, Integer pagesize) {
    PageHelper.startPage(pagenum, pagesize);
    Page<TMwLaneInfo> page = mapper.findAll();
    return page;
  }
}
