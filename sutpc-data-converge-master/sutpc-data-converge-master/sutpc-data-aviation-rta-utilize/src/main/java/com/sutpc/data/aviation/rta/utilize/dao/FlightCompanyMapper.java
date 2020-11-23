package com.sutpc.data.aviation.rta.utilize.dao;

import com.sutpc.data.aviation.rta.utilize.entity.FlightCompany;
import org.springframework.stereotype.Repository;

@Repository
public interface FlightCompanyMapper {

  int deleteByPrimaryKey(String id);

  int insert(FlightCompany record);

  int insertSelective(FlightCompany record);

  FlightCompany selectByPrimaryKey(String id);

  int updateByPrimaryKeySelective(FlightCompany record);

  int updateByPrimaryKey(FlightCompany record);

  FlightCompany selectByShort(String shortNo);
}