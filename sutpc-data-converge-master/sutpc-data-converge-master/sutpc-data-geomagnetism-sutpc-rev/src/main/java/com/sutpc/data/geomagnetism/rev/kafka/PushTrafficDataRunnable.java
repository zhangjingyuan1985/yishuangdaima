package com.sutpc.data.geomagnetism.rev.kafka;

import com.alibaba.fastjson.JSON;
import com.sutpc.data.geomagnetism.rev.bean.TrafficData;
import com.sutpc.data.util.KafkaProducerUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.util.CollectionUtils;

/**
 * Description:kafka单例生产者.
 *
 * @author zhy
 * @date 2019/7/31
 */
@Slf4j
public class PushTrafficDataRunnable implements Runnable {

  private KafkaProducerUtils producerUtils;
  private ConcurrentLinkedQueue<TrafficData> queue;

  private PushTrafficDataRunnable() {
    int poolSize = 10;
    this.producerUtils = new KafkaProducerUtils();
    this.queue = new ConcurrentLinkedQueue<>();
    ExecutorService executorService = new ThreadPoolExecutor(poolSize,
        poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(),
        new BasicThreadFactory.Builder().namingPattern("PushTrafficDataRunner-%d").build());
    for (int i = 0; i < poolSize; i++) {
      executorService.execute(this);
    }
  }

  public static PushTrafficDataRunnable getInstance() {
    return SingletonHolder.INSTANCE;
  }


  public void addAllContent(List<TrafficData> contentList) {
    this.queue.addAll(contentList);
  }


  /**
   * 发送消息.
   *
   * @param msg 消息
   * @return 返回一个结果元数据
   */
  private Future<RecordMetadata> send(String msg) {
    return producerUtils.send(msg);
  }

  @Override
  public void run() {
    boolean flag = true;
    while (flag) {
      if (!queue.isEmpty()) {
        boolean isQueueTooLong = queue.size() > 10000;
        if (isQueueTooLong) {
          log.error("queue is too long,queue size is {}", queue.size());
        }
        int i;
        log.info("Thread name is {},The start Time of cycle is {}",
            Thread.currentThread().getName(), System.currentTimeMillis());
        List<TrafficData> trafficDataList = new ArrayList<>();
        int maxCountOneTime = 1000;
        for (i = 0; i < maxCountOneTime; i++) {
          TrafficData msg = queue.poll();
          if (msg != null) {
            log.info("Msg is {}", msg.toString());
            trafficDataList.add(msg);
          } else {
            break;
          }
        }
        log.info("trafficDataList size is {}", trafficDataList.size());
        if (!CollectionUtils.isEmpty(trafficDataList)) {
          Future<RecordMetadata> sendResult = send(JSON
              .toJSONString(trafficDataList));
          try {
            RecordMetadata recordMetadata = sendResult.get();
            log.info("WorkerForWriteFile send result is  offset:{},partition:{}, keySize:{},"
                    + "timestamp:{},topic:{}", recordMetadata.offset(), recordMetadata.partition(),
                recordMetadata.serializedKeySize(), recordMetadata.timestamp(),
                recordMetadata.topic());
          } catch (InterruptedException | ExecutionException e) {
            flag = false;
            log.error("kafka producer send error", e);
            Thread.currentThread().interrupt();
          }
        }
        log.info("Thread name is {},The end Time of cycle is {}", Thread
            .currentThread().getName(), System.currentTimeMillis());
      } else {
        try {
          Thread.sleep(20);
        } catch (InterruptedException e) {
          flag = false;
          log.error("PushTrafficDataRunner thread encounter error", e);
          Thread.currentThread().interrupt();
        }
      }
    }
  }

  private static class SingletonHolder {

    private static final PushTrafficDataRunnable INSTANCE = new PushTrafficDataRunnable();
  }
}
