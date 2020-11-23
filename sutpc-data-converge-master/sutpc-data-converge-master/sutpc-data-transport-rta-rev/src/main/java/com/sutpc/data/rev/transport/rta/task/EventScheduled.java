package com.sutpc.data.rev.transport.rta.task;

import com.sutpc.data.rev.transport.rta.entity.EventInfo;
import com.sutpc.data.rev.transport.rta.entity.EventPatrol;
import com.sutpc.data.rev.transport.rta.entity.EventStatus;
import com.sutpc.data.rev.transport.rta.res.EventInfoRes;
import com.sutpc.data.rev.transport.rta.res.EventPatrolRes;
import com.sutpc.data.rev.transport.rta.res.EventStatusRes;
import com.sutpc.data.rev.transport.rta.service.EventService;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * 同步定时任务.
 *
 * @Auth smilesnake minyikun
 * @Create 2019/8/15 15:38
 */
@Slf4j
@Component
public class EventScheduled {

  @Autowired
  private EventService eventService;
  private Queue<String> queue = new LinkedBlockingQueue<>();
  private boolean initialize = false;

  /**
   * 同步事件.
   */
  @Scheduled(fixedRate = 10 * 60 * 1000)
  public void syncEventInfos() {
    if (!initialize) {
      // 从数据库加载未同步处理流程的事件id列表
      List<String> eventIds = eventService.getNonSyncEventIds();
      log.info("initialize size = {}", eventIds.size());
      if (!CollectionUtils.isEmpty(eventIds)) {
        queue.addAll(eventIds);
      }
      initialize = true;
    }
    List<EventInfoRes> sources = eventService.getRemoteEventInfos(200);
    log.info("remote size = {}", sources.size());
    if (!CollectionUtils.isEmpty(sources)) {
      List<EventInfo> infos = new ArrayList<>();
      sources.forEach(s -> {
        EventInfo info = new EventInfo();
        BeanUtils.copyProperties(s, info);
        infos.add(info);
      });
      eventService.saveInfos(infos);
      List<String> eventIds = infos.stream().map(EventInfo::getEventId)
          .collect(Collectors.toList());
      queue.addAll(eventIds);
    }
  }

  /**
   * 同步流程.
   */
  @Scheduled(initialDelay = 3 * 1000, fixedRate = 200)
  public void syncEventStatuses() {
    if (queue.isEmpty()) {
      return;
    }
    String eventId = queue.poll();
    log.info("eventId = {}", eventId);
    if (StringUtils.isEmpty(eventId)) {
      return;
    }
    List<EventStatusRes> sources = eventService.getRemoteEventStatuses(eventId);
    log.info("remote size = {}", sources.size());
    if (!CollectionUtils.isEmpty(sources)) {
      List<EventStatus> statuses = new ArrayList<>();
      sources.forEach(s -> {
        EventStatus status = new EventStatus();
        BeanUtils.copyProperties(s, status);
        statuses.add(status);
      });
      eventService.saveStatuses(statuses);
    }
  }

  /**
   * 同步巡查.
   */
  @Scheduled(fixedRate = 60 * 60 * 1000)
  public void syncEventParols() {
    List<EventPatrolRes> sources = eventService.getRemoteEventPatrols();
    log.info("remote size = {}", sources.size());
    if (!CollectionUtils.isEmpty(sources)) {
      List<EventPatrol> patrols = new ArrayList<>();
      sources.forEach(s -> {
        EventPatrol patrol = new EventPatrol();
        BeanUtils.copyProperties(s, patrol);
        patrols.add(patrol);
      });
      eventService.savePatrols(patrols);
    }
  }

}
