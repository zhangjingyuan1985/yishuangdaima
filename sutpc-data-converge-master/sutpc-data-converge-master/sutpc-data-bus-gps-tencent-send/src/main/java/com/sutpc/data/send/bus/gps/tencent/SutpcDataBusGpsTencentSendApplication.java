package com.sutpc.data.send.bus.gps.tencent;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SutpcDataBusGpsTencentSendApplication {

  public static void main(String[] args) {
    SpringApplication.run(SutpcDataBusGpsTencentSendApplication.class, args);
  }

}
