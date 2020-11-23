package com.sutpc.data.rev.expressway.microwave.rta.service;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TMwReport;

/**
 *  .
 * @Auth smilesnake minyikun
 * @Create 2019/8/15 10:59
 */
public interface TMwReportService {
  Page<TMwReport> findAll(Integer pagenum, Integer pagesize);
}
