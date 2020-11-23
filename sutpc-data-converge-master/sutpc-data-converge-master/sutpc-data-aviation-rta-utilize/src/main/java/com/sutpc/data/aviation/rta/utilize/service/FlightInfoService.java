package com.sutpc.data.aviation.rta.utilize.service;

import com.sutpc.data.aviation.rta.utilize.entity.FlightInfo;
import java.util.List;

public interface FlightInfoService {

  /**
   * 插入或者更新数据逻辑处理.
   * @param flightInfos 实体类
   */
  Integer saveOrUpdate(List<FlightInfo> flightInfos);
}
