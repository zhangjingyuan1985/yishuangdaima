package com.sutpc.data.rev.expressway.microwave.rta.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TMwReport;
import com.sutpc.data.rev.expressway.microwave.rta.mapper.TMwReportMapper;
import com.sutpc.data.rev.expressway.microwave.rta.service.TMwReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * .
 *
 * @Auth smilesnake minyikun
 * @Create 2019/8/15 10:59
 */
@Service
public class TMwReportServiceImpl implements TMwReportService {

  @Autowired
  private TMwReportMapper mapper;

  @Transactional(readOnly = true)
  @Override
  public Page<TMwReport> findAll(Integer pagenum, Integer pagesize) {
    PageHelper.startPage(pagenum, pagesize);
    Page<TMwReport> page = mapper.findAll();
    return page;
  }
}
