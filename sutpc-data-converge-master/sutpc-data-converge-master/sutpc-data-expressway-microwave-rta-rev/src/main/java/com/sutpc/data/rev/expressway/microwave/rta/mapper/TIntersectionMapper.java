package com.sutpc.data.rev.expressway.microwave.rta.mapper;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TIntersection;

public interface TIntersectionMapper {
  Page<TIntersection> findAll();

  int deleteByPrimaryKey(String intersectioncode);

  int insert(TIntersection record);

  int insertSelective(TIntersection record);

  TIntersection selectByPrimaryKey(String intersectioncode);

  int updateByPrimaryKeySelective(TIntersection record);

  int updateByPrimaryKeyWithBlobs(TIntersection record);

  int updateByPrimaryKey(TIntersection record);
}