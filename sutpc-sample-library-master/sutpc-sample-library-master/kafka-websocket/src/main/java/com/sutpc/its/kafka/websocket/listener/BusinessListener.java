package com.sutpc.its.kafka.websocket.listener;

import com.sutpc.its.kafka.websocket.ws.WsServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @Description .
 * @Author:ShangxiuWu
 * @Date: 16:02 2020/7/24.
 * @Modified By:
 */
@Slf4j
@Component
public class BusinessListener {

  @KafkaListener(topics = "${spring.kafka.consumer.default-topic}")
  public void listen(String msg) {
    WsServerEndpoint.sendToAll(msg);
  }

}
