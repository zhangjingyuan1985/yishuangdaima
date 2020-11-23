package com.sutpc.data.rev.geomagnetism.tocc.task;

import com.alibaba.fastjson.JSON;
import com.sutpc.data.rev.geomagnetism.tocc.entity.Geomagnetism;
import com.sutpc.data.rev.geomagnetism.tocc.service.GeomagnetismService;
import com.sutpc.data.util.KafkaProducerUtils;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
public class SendKafkaScheduled {

  @Autowired
  private GeomagnetismService service;
  private KafkaProducerUtils producer = new KafkaProducerUtils();

  private static final Logger logger = LoggerFactory.getLogger(SendKafkaScheduled.class);
  private final List<Geomagnetism> geomagnetisms = new ArrayList<>();
  private static Map<String, Integer> mapCode = new HashMap<>();
  private static LocalDate lastDate = null;

  @Value("${send.kafka.geomagnetism.topic}")
  private String topic;

  /**
   * 定时加载地磁信息.
   */
  @Scheduled(fixedRate = 30 * 60 * 1000)
  public void startInfo() {
    List<Geomagnetism> sources = service.getAll();
    logger.info("startInfo -> size = {}", sources.size());
    logger.info("startInfo -> {}", JSON.toJSONString(sources));
    if (!CollectionUtils.isEmpty(sources)) {
      synchronized (geomagnetisms) {
        geomagnetisms.clear();
        geomagnetisms.addAll(sources);
      }
    }
  }

  /**
   * 定时拉取数据.
   */
  @Scheduled(initialDelay = 3 * 1000, fixedRate = 5 * 60 * 1000)
  public void startData() {
    if (lastDate == null) {
      lastDate = LocalDate.now();
    } else {
      if (!lastDate.isEqual(LocalDate.now())) {
        mapCode.clear();
        lastDate = LocalDate.now();
      }
    }
    List<String> codes = new ArrayList<>();
    synchronized (geomagnetisms) {
      codes = geomagnetisms.stream().map(Geomagnetism::getCode).collect(Collectors.toList());
    }
    logger.info("startData -> codes = {}", JSON.toJSONString(codes));
    if (CollectionUtils.isEmpty(codes)) {
      return;
    }
    LocalDate localDate = LocalDate.now();
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd");
    // 当前日期
    String date = dtf.format(localDate);
    for (String code : codes) {
      List<String> sources = service.getDataByCode(code, date);
      logger.info("startData -> code = {}, size = {}", code, sources.size());
      if (!CollectionUtils.isEmpty(sources)) {
        // 排序
        Collections.sort(sources);
        int startIndex = mapCode.getOrDefault(code, 0);
        logger.info("startData -> code = {}, startIndex = {}", code, startIndex);
        if (startIndex < sources.size()) {
          List<String> values = sources.subList(startIndex, sources.size());
          logger.info("startData -> code = {}, remain = {}", code, values.size());
          String message = JSON.toJSONString(values);
          producer.send(topic, message);
          logger.info("startData send -> code = {}", code);
          // logger.info("startData -> code = {}, message = {}", code, message);
          mapCode.put(code, sources.size());
        }
      }
      try {
        // 需要休眠，否则报操作太频繁错误
        Thread.sleep(3000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
  }
}
