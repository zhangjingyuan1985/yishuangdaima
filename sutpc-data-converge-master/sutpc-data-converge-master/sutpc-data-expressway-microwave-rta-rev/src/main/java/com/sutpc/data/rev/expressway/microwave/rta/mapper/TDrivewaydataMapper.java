package com.sutpc.data.rev.expressway.microwave.rta.mapper;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TDrivewaydata;

public interface TDrivewaydataMapper {
  Page<TDrivewaydata> findAll();

  int deleteByPrimaryKey(Long flowid);

  int insert(TDrivewaydata record);

  int insertSelective(TDrivewaydata record);

  TDrivewaydata selectByPrimaryKey(Long flowid);

  int updateByPrimaryKeySelective(TDrivewaydata record);

  int updateByPrimaryKey(TDrivewaydata record);
}