package com.sutpc.data.send.bus.gps.tencent.controller;

import com.alibaba.fastjson.JSONObject;
import com.sutpc.data.util.KafkaConsumerUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * .
 *
 * @Auth smilesnake minyikun
 * @Create 2019/8/8 10:09
 */
@Slf4j
@RestController
@RequestMapping("/kafka")
public class KafkaConsumerController {

  private LinkedBlockingQueue queue = new LinkedBlockingQueue(1000000);

  /**
   * .
   */
  @GetMapping("/get")
  public JSONObject consumer() {
    JSONObject obj = new JSONObject();
    obj.put("message", "success");
    obj.put("status", 200);
    List<Object> list = new ArrayList<>();
    int size = queue.size() > 1000 ? 1000 : queue.size();
    for (int i = 0; i < size; i++) {
      list.add(queue.poll());
    }
    obj.put("body", list);
    return obj;
  }

  @PostConstruct
  private void queue() {
    ExecutorService executor = new ThreadPoolExecutor(1, 1, 60,
        TimeUnit.SECONDS, new LinkedBlockingQueue<>(1024),
        new BasicThreadFactory.Builder().namingPattern("kafka-consumer-enqueue-pool-%d")
            .daemon(false).build(), new AbortPolicy());
    executor.execute(() -> new KafkaConsumerUtils().receive(record -> {
      log.info("queue size:{}", queue.size());
      log.info("offset:{},value:{},timestamp:{},currentTime:P{}", record.offset(), record.value(),
          record.timestamp(), new Date());
      //没满直接加
      if (queue.size() < 1000000) {
        queue.offer(record.value());
      } else {
        //队列满了，阻塞操作，丢弃老数据，然后储放新数据
        try {
          queue.take();
          queue.put(record.value());
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }));
  }
}
