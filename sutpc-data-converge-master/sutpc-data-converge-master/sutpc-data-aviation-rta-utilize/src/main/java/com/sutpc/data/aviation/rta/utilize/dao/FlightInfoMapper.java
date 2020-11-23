package com.sutpc.data.aviation.rta.utilize.dao;

import com.sutpc.data.aviation.rta.utilize.entity.FlightInfo;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightInfoMapper {

  int deleteByPrimaryKey(String id);

  int insert(FlightInfo record);

  int insertSelective(FlightInfo record);

  int insertBatch(@Param("list") List<FlightInfo> record);

  FlightInfo selectByPrimaryKey(String id);

  int updateByPrimaryKeySelective(FlightInfo record);
  int updateBatch(@Param("list") List<FlightInfo> list);

  int updateByPrimaryKey(FlightInfo record);

  FlightInfo selectExistInfo(FlightInfo record);

  int updateDeleteStatus(@Param("ids") List<String> ids);
}