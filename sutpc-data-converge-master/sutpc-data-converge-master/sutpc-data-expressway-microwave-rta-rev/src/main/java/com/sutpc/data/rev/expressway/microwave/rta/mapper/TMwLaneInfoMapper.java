package com.sutpc.data.rev.expressway.microwave.rta.mapper;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TMwLaneInfo;

public interface TMwLaneInfoMapper {
  Page<TMwLaneInfo> findAll();

  int deleteByPrimaryKey(Long terminalid);

  int insert(TMwLaneInfo record);

  int insertSelective(TMwLaneInfo record);

  TMwLaneInfo selectByPrimaryKey(Long terminalid);

  int updateByPrimaryKeySelective(TMwLaneInfo record);

  int updateByPrimaryKey(TMwLaneInfo record);
}