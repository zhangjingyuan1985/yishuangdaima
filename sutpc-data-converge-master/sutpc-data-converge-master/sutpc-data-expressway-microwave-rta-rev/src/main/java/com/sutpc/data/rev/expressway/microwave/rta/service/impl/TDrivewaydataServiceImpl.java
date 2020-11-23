package com.sutpc.data.rev.expressway.microwave.rta.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TDrivewaydata;
import com.sutpc.data.rev.expressway.microwave.rta.mapper.TDrivewaydataMapper;
import com.sutpc.data.rev.expressway.microwave.rta.service.TDrivewaydataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 *  .
 * @Auth smilesnake minyikun
 * @Create 2019/8/15 9:19
 */
@Service
public class TDrivewaydataServiceImpl implements TDrivewaydataService {
  @Autowired
  private TDrivewaydataMapper mapper;

  @Transactional(readOnly = true)
  @Override
  public Page<TDrivewaydata> findAll(Integer pagenum, Integer pagesize) {
    PageHelper.startPage(pagenum, pagesize);
    Page<TDrivewaydata> page = mapper.findAll();
    return page;
  }
}
