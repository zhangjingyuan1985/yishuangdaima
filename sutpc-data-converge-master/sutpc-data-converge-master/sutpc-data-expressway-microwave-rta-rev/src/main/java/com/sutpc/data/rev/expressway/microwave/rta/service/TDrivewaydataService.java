package com.sutpc.data.rev.expressway.microwave.rta.service;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TDrivewaydata;

/**
 *  .
 * @Auth smilesnake minyikun
 * @Create 2019/8/15 9:17
 */
public interface TDrivewaydataService {
  Page<TDrivewaydata> findAll(Integer pagenum, Integer pagesize);
}
