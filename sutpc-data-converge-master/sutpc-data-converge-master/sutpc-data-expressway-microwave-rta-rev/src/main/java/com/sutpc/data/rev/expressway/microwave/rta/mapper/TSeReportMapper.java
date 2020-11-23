package com.sutpc.data.rev.expressway.microwave.rta.mapper;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TSeReport;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TSeReportKey;

public interface TSeReportMapper {
  Page<TSeReport> findAll();

  int deleteByPrimaryKey(TSeReportKey key);

  int insert(TSeReport record);

  int insertSelective(TSeReport record);

  TSeReport selectByPrimaryKey(TSeReportKey key);

  int updateByPrimaryKeySelective(TSeReport record);

  int updateByPrimaryKey(TSeReport record);
}