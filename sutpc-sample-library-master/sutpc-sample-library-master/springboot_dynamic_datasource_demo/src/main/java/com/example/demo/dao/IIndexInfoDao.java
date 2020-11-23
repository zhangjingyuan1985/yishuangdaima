package com.example.demo.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;

/**
 * .
 *
 * @Author:Steven.Yi
 * @Date: 08:34$ 20200424$.
 * @Description
 * @Modified By:
 */
@Mapper
public interface IIndexInfoDao {

  List<Map<String, Object>> findObjects();
}
