package com.sutpc.data.rev.expressway.microwave.rta.mapper;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TSeQueueHis;

public interface TSeQueueHisMapper {
  Page<TSeQueueHis> findAll();

  int deleteByPrimaryKey(String intersectioncode);

  int insert(TSeQueueHis record);

  int insertSelective(TSeQueueHis record);

  TSeQueueHis selectByPrimaryKey(String intersectioncode);

  int updateByPrimaryKeySelective(TSeQueueHis record);

  int updateByPrimaryKey(TSeQueueHis record);
}