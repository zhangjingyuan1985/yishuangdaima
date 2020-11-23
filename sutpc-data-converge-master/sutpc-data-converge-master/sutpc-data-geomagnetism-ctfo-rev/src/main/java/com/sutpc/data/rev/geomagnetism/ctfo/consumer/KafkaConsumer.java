package com.sutpc.data.rev.geomagnetism.ctfo.consumer;

/**
 * @description:
 * @author:tangshaofeng
 * @createtime:2019/9/4 17:24
 */

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.sutpc.data.util.KafkaConsumerUtils;
import com.sutpc.data.util.KafkaProducerUtils;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


@Slf4j
@Component
public class KafkaConsumer {

  private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue(1000000);
  private final String nullStr = "null";


  /**
   * .
   */
  @PostConstruct
  public void start() {
    ThreadFactory threadFactory = new ThreadFactoryBuilder().setNameFormat("kafka-consume-pool-%d")
        .build();
    ExecutorService executorService = new ThreadPoolExecutor(2, 2, 6000, TimeUnit.SECONDS,
        new LinkedBlockingQueue<>(1024), threadFactory, new ThreadPoolExecutor.AbortPolicy());
    executorService.execute(() -> KafkaConsumerUtils.zookeeperConnReceive(msg -> {
      if (!StringUtils.isEmpty(msg) && !msg.equals(nullStr)) {
        queue.offer(msg);
      }
    }));
    executorService.execute(() -> consumer());
  }

  /**
   * .
   */
  public void consumer() {
    KafkaProducerUtils utils = new KafkaProducerUtils();
    //消费消息
    while (true) {
      if (!queue.isEmpty()) {
        log.info("size:{}", queue.size());
        int size = queue.size() > 1000 ? 1000 : queue.size();
        for (int i = 0; i < size; i++) {
          String str = queue.poll();
          utils.send(str);
        }
      }
    }
  }
}
