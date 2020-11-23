package com.sutpc.data.rev.phone.signaling.rta.task;

import com.alibaba.fastjson.JSONObject;
import com.sutpc.data.rev.phone.signaling.rta.entity.TzPfPdSaturation;
import com.sutpc.data.rev.phone.signaling.rta.service.TzPfPdSaturationService;
import com.sutpc.data.util.KafkaProducerUtils;
import java.time.LocalDateTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
public class SendKafkaTzPfPdScheduled {

  @Autowired
  private TzPfPdSaturationService service;
  private KafkaProducerUtils producer = new KafkaProducerUtils();

  @Value("${send.kafka.tzpfpd.topic}")
  private String topic;
  private LocalDateTime lastTimestamp = null;

  private static final Logger logger = LoggerFactory.getLogger(SendKafkaTzPfPdScheduled.class);

  /**
   * .
   */
  @Scheduled(cron = "${send.kafka.tzpfpd.cron}")
  public void startForTzPfPd() {
    logger.info("startForTzPfPd >> start");
    List<TzPfPdSaturation> saturations = null;
    try {
      saturations = service.queryForCurrent();
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("startForTzPfPd queryForCurrent >> {}", e);
    }
    if (CollectionUtils.isEmpty(saturations)) {
      return;
    }
    String value = JSONObject.toJSONString(saturations);
    logger.info("startForTzPfPd saturations >> {}", value);
    TzPfPdSaturation saturation = saturations.get(0);
    logger.info("startForTzPfPd last = {}, current = {}", lastTimestamp, saturation.getTimestamp());
    if (lastTimestamp != null && lastTimestamp.isEqual(saturation.getTimestamp())) {
      return;
    }
    try {
      producer.send(topic, value);
      logger.info("startForTzPfPd send >> {}", saturations.size());
      lastTimestamp = saturation.getTimestamp();
    } catch (Exception e) {
      e.printStackTrace();
      logger.error("startForTzPfPd send >> {}", e);
    }
    logger.info("startForTzPfPd >> end");
  }
}
