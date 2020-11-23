package com.sutpc.data.rev.bus.gps.source;

import com.google.protobuf.InvalidProtocolBufferException;
import com.sutpc.data.rev.bus.gps.model.GpsVo;
import com.sutpc.data.rev.bus.gps.parse.GpsMessageParser;
import com.sutpc.data.rev.bus.gps.sink.FileSink;
import com.sutpc.data.rev.bus.gps.stat.GpsCounter;
import com.sutpc.data.rev.bus.gps.util.ValidateUtils;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class RabbitMqListener {

  @Autowired
  public FileSink fileSink;

  @Autowired
  public KafkaTemplate suptcKafkaTemplate;

  /**
   * .
   */
  @RabbitHandler
  @RabbitListener(queues = "${spring.rabbitmq.queue}", containerFactory = "multiListenerContainer")
  public void process(@Payload byte[] body) throws InvalidProtocolBufferException {
    log.debug("消息长度 ", body.length);
    GpsVo gpsvo = GpsMessageParser.parse(body);
    if (Objects.nonNull(gpsvo) && ValidateUtils.gbsBus(gpsvo)) {
      log.debug("收到消息：{}", gpsvo.toString());

      log.debug("生产到kafka...");
      suptcKafkaTemplate.sendDefault(gpsvo.toString());

      GpsCounter.longAdder.increment();

      log.debug("加入缓存队列...");
      //fileSink.addQueue(gpsvo.toString());
    }

  }
}
