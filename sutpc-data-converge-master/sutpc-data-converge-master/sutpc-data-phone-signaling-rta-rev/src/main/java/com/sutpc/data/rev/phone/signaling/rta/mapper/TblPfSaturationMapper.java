package com.sutpc.data.rev.phone.signaling.rta.mapper;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.phone.signaling.rta.entity.TblPfSaturation;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface TblPfSaturationMapper {

  Page<TblPfSaturation> findAll();

  int deleteByPrimaryKey(Long tazid);

  int insert(TblPfSaturation record);

  int insertSelective(TblPfSaturation record);

  TblPfSaturation selectByPrimaryKey(Long tazid);

  int updateByPrimaryKeySelective(TblPfSaturation record);

  int updateByPrimaryKey(TblPfSaturation record);
}