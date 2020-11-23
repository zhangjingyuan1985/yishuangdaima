package com.sutpc.data.rev.expressway.microwave.rta.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TMwTerminalDataSta;
import com.sutpc.data.rev.expressway.microwave.rta.mapper.TMwTerminalDataStaMapper;
import com.sutpc.data.rev.expressway.microwave.rta.service.TMwTerminalDataStaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * .
 *
 * @Auth smilesnake minyikun
 * @Create 2019/8/15 16:48
 */
@Slf4j
@Service
public class TMwTerminalDataStaServiceImpl implements TMwTerminalDataStaService {

  @Autowired
  private TMwTerminalDataStaMapper mapper;

  @Transactional(readOnly = true)
  @Override
  public Page<TMwTerminalDataSta> findAll(Integer pagenum, Integer pagesize) {
    PageHelper.startPage(pagenum, pagesize);
    Page<TMwTerminalDataSta> page = mapper.findAll();
    return page;
  }

  @Override
  public Page<TMwTerminalDataSta> findByCollectionTime(Integer pagenum, Integer pagesize,
      String startTime, String endTime) {
    PageHelper.startPage(pagenum, pagesize);
    Page<TMwTerminalDataSta> page = mapper.findByCollectionTime(startTime, endTime);
    return page;
  }
}
