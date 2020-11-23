package com.sutpc.its.kafka.websocket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * @Description .
 * @Author:ShangxiuWu
 * @Date: 16:12 2020/7/24.
 * @Modified By:
 */
@Configuration
public class WebSocketConfig {

  @Bean
  public ServerEndpointExporter serverEndpointExporter() {
    return new ServerEndpointExporter();
  }
}
