package com.sutpc.data.rev.ic.card.szt.rabbitmq;

import com.sutpc.data.util.KafkaProducerUtils;
import java.io.UnsupportedEncodingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class RabbitMqListener {

  private KafkaProducerUtils producer = new KafkaProducerUtils();

  /**
   * .
   *
   * @param message 信息
   */
  @RabbitHandler
  @RabbitListener(queues = "${spring.rabbitmq.template.default-receive-queue}",
      containerFactory = "multiListenerContainer")
  public void process(@Payload byte[] message) {
    try {
      String msg = new String(message, "UTF-8");
      //log.info("basic consumer receive message:{msg = [" + msg + "]}");
      producer.send(msg);
    } catch (UnsupportedEncodingException e) {
      log.error("error message:{}", e.getMessage());
    } catch (Exception e) {
      log.error("error message:{}", e.getMessage());
    }
  }
}
