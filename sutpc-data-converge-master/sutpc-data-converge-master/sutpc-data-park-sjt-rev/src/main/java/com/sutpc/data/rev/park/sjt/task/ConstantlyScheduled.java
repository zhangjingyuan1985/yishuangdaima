package com.sutpc.data.rev.park.sjt.task;

import com.alibaba.fastjson.JSON;
import com.sutpc.data.rev.park.sjt.entity.ConstantlyRes;
import com.sutpc.data.rev.park.sjt.entity.ParkLeft;
import com.sutpc.data.rev.park.sjt.mapper.ParkLeftMapper;
import com.sutpc.data.rev.park.sjt.service.RtcService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * 同步定时任务.
 *
 * @Auth smilesnake minyikun
 * @Create 2019/8/15 15:38
 */
@Slf4j
@Component
public class ConstantlyScheduled {

  @Autowired
  private RtcService service;
  @Autowired
  private ParkLeftMapper mapper;
  private static final Logger logger = LoggerFactory.getLogger(ConstantlyScheduled.class);

  /**
   * 定时拉取数据.
   */
  @Scheduled(cron = "0 15 */1 * * ?")
  //@Scheduled(initialDelay = 3 * 1000, fixedRate = 5 * 60 * 1000)
  public void startData() {
    logger.info("startData start");
    List<ConstantlyRes> constantlies = service.getConstantlyBerth();
    if (CollectionUtils.isEmpty(constantlies)) {
      int count = 0;
      while (count < 5) {
        constantlies = service.getConstantlyBerth();
        if (!CollectionUtils.isEmpty(constantlies)) {
          break;
        }
        count++;
      }
    }
    LocalDateTime current = LocalDateTime.now();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
    int date = Integer.parseInt(dtf.format(current));
    dtf = DateTimeFormatter.ofPattern("HH");
    int period = Integer.parseInt(dtf.format(current));
    logger.info("startData constantlies = {}", JSON.toJSONString(constantlies));
    if (!CollectionUtils.isEmpty(constantlies)) {
      List<ParkLeft> parks = new ArrayList<>();
      for (ConstantlyRes constantly : constantlies) {
        ParkLeft park = new ParkLeft();
        park.setDate(date);
        park.setPeriod(period);
        park.setFid(constantly.getSectionId());
        park.setLeftNum(Integer.parseInt(constantly.getBerthVacant()));
        park.setSaturation(Float.parseFloat(constantly.getLoadIndex()));
        parks.add(park);
      }
      logger.info("startData size = {}", parks.size());
      mapper.insert(parks);
    }
    logger.info("startData end");
  }

}
