package com.sutpc.data.rev.expressway.microwave.rta.mapper;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TManagerRole;

public interface TManagerRoleMapper {
  Page<TManagerRole> findAll();

  int deleteByPrimaryKey(Long id);

  int insert(TManagerRole record);

  int insertSelective(TManagerRole record);

  TManagerRole selectByPrimaryKey(Long id);

  int updateByPrimaryKeySelective(TManagerRole record);

  int updateByPrimaryKey(TManagerRole record);
}