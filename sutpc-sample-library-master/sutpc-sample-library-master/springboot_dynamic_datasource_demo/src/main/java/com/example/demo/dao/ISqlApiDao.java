package com.example.demo.dao;

import com.example.demo.model.SqlApiEntity;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;

/**
 * .
 *
 * @Author:Steven.Yi
 * @Date: 19:02$ 20200704$.
 * @Description
 * @Modified By:
 */
@Mapper
public interface ISqlApiDao {

  List<SqlApiEntity> findObjects();
}
