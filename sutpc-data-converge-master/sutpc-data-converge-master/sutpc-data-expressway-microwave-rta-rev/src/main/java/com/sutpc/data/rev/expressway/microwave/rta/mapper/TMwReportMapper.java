package com.sutpc.data.rev.expressway.microwave.rta.mapper;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TMwReport;

public interface TMwReportMapper {
  Page<TMwReport> findAll();

  int deleteByPrimaryKey(Long datetype);

  int insert(TMwReport record);

  int insertSelective(TMwReport record);

  TMwReport selectByPrimaryKey(Long datetype);

  int updateByPrimaryKeySelective(TMwReport record);

  int updateByPrimaryKey(TMwReport record);
}