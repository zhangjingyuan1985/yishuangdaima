package com.sutpc.data.rev.expressway.microwave.rta.mapper;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TManager;

public interface TManagerMapper {
  Page<TManager> findAll();

  int deleteByPrimaryKey(Long id);

  int insert(TManager record);

  int insertSelective(TManager record);

  TManager selectByPrimaryKey(Long id);

  int updateByPrimaryKeySelective(TManager record);

  int updateByPrimaryKey(TManager record);
}