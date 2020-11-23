package com.sutpc.data.aviation.rta.utilize.service.impl;

import com.sutpc.data.aviation.rta.utilize.dao.FlightCompanyMapper;
import com.sutpc.data.aviation.rta.utilize.dao.FlightInfoMapper;
import com.sutpc.data.aviation.rta.utilize.entity.FlightCompany;
import com.sutpc.data.aviation.rta.utilize.entity.FlightInfo;
import com.sutpc.data.aviation.rta.utilize.service.FlightInfoService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @Auth smilesnake minyikun
 * @Create 2019/12/30 15:40 
 */
@Service
public class FlightInfoServiceImpl implements FlightInfoService {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm:ss");

  @Autowired
  private FlightInfoMapper mapper;
  @Autowired
  private FlightCompanyMapper companyMapper;

  @Override
  public Integer saveOrUpdate(List<FlightInfo> flightInfos) {
    List<String> ids = new ArrayList<>();
    AtomicInteger count = new AtomicInteger(0);
    List<FlightInfo> insert = new ArrayList<>();
    List<FlightInfo> update = new ArrayList<>();
    for (FlightInfo info : flightInfos) {
      FlightInfo temp = mapper.selectExistInfo(info);
      if (temp == null) {
        info.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        info.setCreatetime(FORMATTER.format(LocalDateTime.now()));
        info.setIsdelete("0");
        String shortNo = info.getFlightno().substring(0, 2);
        FlightCompany company = companyMapper.selectByShort(shortNo);
        if (company != null && company.getId() != null) {
          info.setCompanyid(company.getId());
        }

        mapper.insertSelective(info);
        insert.add(info);
      } else {
        temp.setDeptime(info.getDeptime());
        temp.setArrtime(info.getArrtime());
        temp.setFstatus(info.getFstatus());
        temp.setIsdelete("0");
        temp.setUpdatetime(FORMATTER.format(LocalDateTime.now()));
        temp.setFromcity(info.getFromcity());
        temp.setTocity(info.getTocity());

        update.add(temp);
        ids.add(temp.getId());
      }
    }
    mapper.updateDeleteStatus(ids);
    return ids.size();
  }
}
