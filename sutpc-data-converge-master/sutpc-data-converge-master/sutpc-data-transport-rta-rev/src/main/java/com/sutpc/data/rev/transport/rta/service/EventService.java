package com.sutpc.data.rev.transport.rta.service;

import com.sutpc.data.rev.transport.rta.entity.EventInfo;
import com.sutpc.data.rev.transport.rta.entity.EventPatrol;
import com.sutpc.data.rev.transport.rta.entity.EventStatus;
import com.sutpc.data.rev.transport.rta.res.EventInfoRes;
import com.sutpc.data.rev.transport.rta.res.EventPatrolRes;
import com.sutpc.data.rev.transport.rta.res.EventStatusRes;
import java.util.List;

/**
 * .
 *
 * @author admin
 * @date 2020/8/3 9:24
 */
public interface EventService {

  List<EventInfoRes> getRemoteEventInfos(int size);

  void saveInfos(List<EventInfo> infos);

  List<String> getNonSyncEventIds();

  List<EventStatusRes> getRemoteEventStatuses(String eventId);

  void saveStatuses(List<EventStatus> statuses);

  List<EventPatrolRes> getRemoteEventPatrols();

  void savePatrols(List<EventPatrol> patrols);

}
