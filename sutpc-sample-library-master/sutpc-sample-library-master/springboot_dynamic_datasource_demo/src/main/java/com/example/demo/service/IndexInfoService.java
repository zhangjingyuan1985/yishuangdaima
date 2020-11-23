package com.example.demo.service;

import com.example.demo.dao.IIndexInfoDao;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * .
 *
 * @Author:Steven.Yi
 * @Date: 08:40$ 20200424$.
 * @Description
 * @Modified By:
 */
@Service
public class IndexInfoService {

  @Autowired
  private IIndexInfoDao indexInfoDao;

  public List<Map<String, Object>> findObjects() {
    return indexInfoDao.findObjects();
  }
}
