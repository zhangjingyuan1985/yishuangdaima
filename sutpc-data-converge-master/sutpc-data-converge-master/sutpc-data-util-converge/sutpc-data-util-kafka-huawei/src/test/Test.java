package com.example.demo.aaa;

import com.sutpc.data.util.KafkaHuaweiConsumerUtils;
import com.sutpc.data.util.KafkaHuaweiProducerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Auth smilesnake minyikun
 * @Create 2019/9/23 16:10
 */
@Slf4j
@Component
public class Test {
  /**
   * kafka生产
   */
  @PostConstruct
  public void start() {
    ExecutorService executorService = Executors.newFixedThreadPool(2);
    Queue<String> queue = new LinkedBlockingQueue<>();
    for (int i = 0; i < 100; i++) {
      queue.offer(UUID.randomUUID().toString().replace("-", ""));
    }
    executorService.execute(() -> {
      KafkaHuaweiProducerUtils producer = new KafkaHuaweiProducerUtils();
      while (true) {
        try {

          if (!queue.isEmpty()) {
            log.info("size:{}", queue.size());
            int size = queue.size() > 1000 ? 1000 : queue.size();
            for (int i = 0; i < size; i++) {
              String value = queue.poll();
              //可用的数据
              producer.send(value);
            }
            log.info("send success ");
          }
        } catch (Exception e) {
          log.error("error:{}", e);
        }
      }
    });
    executorService.execute(() -> new KafkaHuaweiConsumerUtils().receive(record -> {
      log.debug("offset = {},value = {},timestamp:{},current time:{}", record.offset(), record.value(), record.timestamp(), new Date());
      //总数据量
    }));
  }
}
