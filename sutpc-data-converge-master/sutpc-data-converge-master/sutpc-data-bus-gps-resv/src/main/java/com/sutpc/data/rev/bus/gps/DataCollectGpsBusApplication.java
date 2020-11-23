package com.sutpc.data.rev.bus.gps;

import com.sutpc.data.rev.bus.gps.stat.GpsCounter;
import java.util.Date;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@Slf4j
@EnableRabbit
@ComponentScan
@SpringBootApplication
@EnableScheduling
public class DataCollectGpsBusApplication {

  /**
   *  .
   */
  public static void main(String[] args) throws InterruptedException {

    runJob();

    //        TimeUnit.SECONDS.sleep(200);
    SpringApplication.run(DataCollectGpsBusApplication.class, args);


  }

  private static void runJob() {

    ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(
        1,
        new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(false).build());
    // 第一个参数是任务，第二个参数为首次执行的延时时间，第三个参数为定时执行的间隔时间,第四个参数是时间单位

    scheduledThreadPoolExecutor.scheduleAtFixedRate(() -> {

      //每五分钟计数 写到数据库
      log.warn(FastDateFormat.getInstance("yyyyMMdd HH:mm:ss").format(new Date()) + "："
          + GpsCounter.longAdder.longValue());

      GpsCounter.longAdder.reset();

    }, 0L, 5L, TimeUnit.MINUTES);

  }


}
