package com.sutpc.data.rev.expressway.microwave.rta.service;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TSeReport;

public interface TSeReportService {
  Page<TSeReport> findAll(Integer pagenum, Integer pagesize);
}
