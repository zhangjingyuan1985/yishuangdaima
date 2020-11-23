package com.sutpc.data.rev.expressway.microwave.rta.mapper;

import com.github.pagehelper.Page;
import com.sutpc.data.rev.expressway.microwave.rta.entity.TMwTerminalDataSta;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

@Repository
@Mapper
public interface TMwTerminalDataStaMapper {

  Page<TMwTerminalDataSta> findAll();

  Page<TMwTerminalDataSta> findByCollectionTime(@Param("startTime") String startTime,
      @Param("endTime") String endTime);

  int insert(TMwTerminalDataSta record);

  int insertSelective(TMwTerminalDataSta record);
}