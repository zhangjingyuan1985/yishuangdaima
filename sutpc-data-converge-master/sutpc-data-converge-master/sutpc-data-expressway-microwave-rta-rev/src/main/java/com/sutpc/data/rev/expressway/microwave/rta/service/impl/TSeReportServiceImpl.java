package com.sutpc.data.rev.expressway.microwave.rta.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TSeReport;
import com.sutpc.data.rev.expressway.microwave.rta.mapper.TSeReportMapper;
import com.sutpc.data.rev.expressway.microwave.rta.service.TSeReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TSeReportServiceImpl implements TSeReportService {
  @Autowired
  private TSeReportMapper mapper;

  @Transactional(readOnly = true)
  @Override
  public Page<TSeReport> findAll(Integer pagenum, Integer pagesize) {
    PageHelper.startPage(pagenum, pagesize);
    Page<TSeReport> page = mapper.findAll();
    return page;
  }
}
