package com.sutpc.data.rev.geomagnetism.tocc.service;

import com.sutpc.data.rev.geomagnetism.tocc.entity.Geomagnetism;
import java.util.List;

/**
 * 地磁信息服务.
 *
 * @author admin
 * @date 2020/6/16 18:39
 */

public interface GeomagnetismService {

  /**
   * 获取所有地磁信息.
   */
  List<Geomagnetism> getAll();

  /**
   * 获取地磁数据信息.
   */
  List<String> getDataByCode(String code, String date);

}
