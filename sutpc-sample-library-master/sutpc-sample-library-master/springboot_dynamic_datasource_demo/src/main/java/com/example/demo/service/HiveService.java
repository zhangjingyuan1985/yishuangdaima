package com.example.demo.service;

import com.example.demo.config.DataSourceContextHolder;
import com.example.demo.config.DataSourceType;
import com.example.demo.dao.IHiveDao;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * .
 *
 * @Author:Steven.Yi
 * @Date: 14:00$ 20200424$.
 * @Description
 * @Modified By:
 */
@Service
public class HiveService {

  @Autowired
  private IHiveDao hiveDao;

  public List<Map<String, Object>> findObjects(Map<String, Object> map) {
    DataSourceContextHolder.setDataSourceType(DataSourceType.DATASOURCE2.name());
    List<Map<String, Object>> list = hiveDao.findObjects(map);
    DataSourceContextHolder.removeDataSourceType();
    return list;
  }

  public List<Map<String, Object>> findDataBases(Map<String, Object> map) {
    DataSourceContextHolder.setDataSourceType(DataSourceType.DATASOURCE1.name());
    List<Map<String, Object>> list = hiveDao.findDataBases();
    DataSourceContextHolder.removeDataSourceType();
    return list;
  }
}
