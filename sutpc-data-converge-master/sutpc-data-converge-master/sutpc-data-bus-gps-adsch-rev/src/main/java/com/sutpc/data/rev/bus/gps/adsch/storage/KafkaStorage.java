package com.sutpc.data.rev.bus.gps.adsch.storage;

import com.sutpc.data.rev.bus.gps.adsch.util.GpsConvertUtils;
import com.sutpc.data.rev.bus.gps.adsch.util.SpringUtils;
import com.sutpc.data.util.KafkaHuaweiProducerUtils;

import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.StringUtils;

/**
 * kafka存储.
 */
@Slf4j
@Data
public class KafkaStorage {

  KafkaHuaweiProducerUtils producer = null;

  private int threadSize = 8;

  private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue();

  /**
   * 队列累积的次数.
   */
  private AtomicInteger cumulativeQuantity = new AtomicInteger(0);

  private static class SingletonHolder {

    private static final KafkaStorage INSTANCE = new KafkaStorage();
  }

  public static KafkaStorage getInstance() {
    return KafkaStorage.SingletonHolder.INSTANCE;
  }

  /**
   * 启动kafka.
   */
  public KafkaStorage() {
    producer = new KafkaHuaweiProducerUtils();
    ExecutorService executorService = Executors.newFixedThreadPool(1);
    executorService.execute(this::run);
  }

  private void run() {
    while (true) {
      try {
        if (!queue.isEmpty()) {
          if (cumulativeQuantity.getAndIncrement() % 1000 == 0) {
            log.info("quenue size,{}", queue.size());
          }
          String value = queue.poll();
          if (!StringUtils.isEmpty(value)) {
            producer.send(value);
          }
        }
      } catch (Exception e) {
        log.error("error:{}", e);
      }
    }
  }
}
