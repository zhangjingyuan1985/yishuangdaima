package com.sutpc.data.file.bus.gps.pta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SutpcDataBusGpsRevApplication {

  public static void main(String[] args) {
    SpringApplication.run(SutpcDataBusGpsRevApplication.class, args);
  }

}
