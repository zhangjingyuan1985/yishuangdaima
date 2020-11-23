package com.sutpc.data.rev.transport.rta.service.impl;

import com.sutpc.data.rev.transport.rta.entity.EventInfo;
import com.sutpc.data.rev.transport.rta.entity.EventPatrol;
import com.sutpc.data.rev.transport.rta.entity.EventStatus;
import com.sutpc.data.rev.transport.rta.mapper.EventInfoMapper;
import com.sutpc.data.rev.transport.rta.mapper.EventPatrolMapper;
import com.sutpc.data.rev.transport.rta.mapper.EventStatusMapper;
import com.sutpc.data.rev.transport.rta.remote.RemoteHelper;
import com.sutpc.data.rev.transport.rta.remote.RemoteResult;
import com.sutpc.data.rev.transport.rta.res.EventInfoRes;
import com.sutpc.data.rev.transport.rta.res.EventPatrolRes;
import com.sutpc.data.rev.transport.rta.res.EventStatusRes;
import com.sutpc.data.rev.transport.rta.service.EventService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * .
 *
 * @author admin
 * @date 2020/8/3 9:24
 */
@Service
public class EventServiceImpl implements EventService {

  @Autowired
  private RemoteHelper helper;
  @Autowired
  private EventInfoMapper eventInfoMapper;
  @Autowired
  private EventStatusMapper eventStatusMapper;
  @Autowired
  private EventPatrolMapper eventPatrolMapper;
  private static final int MAX_PAGE_SIZE = 200;

  @Override
  public List<EventInfoRes> getRemoteEventInfos(int size) {
    size = size < 1 ? MAX_PAGE_SIZE : (size > MAX_PAGE_SIZE ? MAX_PAGE_SIZE : size);
    Map<String, Object> params = new HashMap<>();
    params.put("pageSize", size);
    RemoteResult result = helper.post("/getEventList", params);
    List<EventInfoRes> res = new ArrayList<>();
    if (result.isState()) {
      res = result.parse(EventInfoRes.class);
    }
    res.forEach(r -> {
      if (!StringUtils.isEmpty(r.getDistrictName()) && !r.getDistrictName().endsWith("区")) {
        r.setDistrictName(r.getDistrictName() + "区");
      }
    });
    return res;
  }

  @Override
  public void saveInfos(List<EventInfo> infos) {
    eventInfoMapper.insert(infos);
  }

  @Override
  public List<String> getNonSyncEventIds() {
    return eventInfoMapper.selectNonSyncEventIds();
  }

  @Override
  public List<EventStatusRes> getRemoteEventStatuses(String eventId) {
    Map<String, Object> params = new HashMap<>();
    params.put("eventID", eventId);
    RemoteResult result = helper.post("/getFlowInfo", params);
    List<EventStatusRes> res = new ArrayList<>();
    if (result.isState()) {
      res = result.parse(EventStatusRes.class);
    }
    return res;
  }

  @Override
  public void saveStatuses(List<EventStatus> statuses) {
    eventStatusMapper.insert(statuses);
  }

  @Override
  public List<EventPatrolRes> getRemoteEventPatrols() {
    RemoteResult result = helper.post("/getPatrolInfo", null);
    List<EventPatrolRes> res = new ArrayList<>();
    if (result.isState()) {
      res = result.parse(EventPatrolRes.class);
    }
    return res;
  }

  @Override
  public void savePatrols(List<EventPatrol> patrols) {
    eventPatrolMapper.insert(patrols);
  }
}
