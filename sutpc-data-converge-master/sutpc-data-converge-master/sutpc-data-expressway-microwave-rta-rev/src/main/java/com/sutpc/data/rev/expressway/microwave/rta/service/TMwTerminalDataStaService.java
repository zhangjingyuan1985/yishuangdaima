package com.sutpc.data.rev.expressway.microwave.rta.service;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TMwTerminalDataSta;
import org.apache.ibatis.annotations.Param;

/**
 * .
 *
 * @Auth smilesnake minyikun
 * @Create 2019/8/15 16:47
 */
public interface TMwTerminalDataStaService {

  Page<TMwTerminalDataSta> findAll(Integer pagenum, Integer pagesize);

  Page<TMwTerminalDataSta> findByCollectionTime(Integer pagenum, Integer pagesize,
      @Param("startTime") String startTime, @Param("endTime") String endTime);
}
