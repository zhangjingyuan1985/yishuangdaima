package com.sutpc.data.rev.expressway.microwave.rta.config;

import java.util.concurrent.Executors;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

/**
 * 多线程执行定时任务.
 *
 * @Auth smilesnake minyikun
 * @Create 2019/8/15 17:40
 */
//@Configuration
public class ScheduleConfig implements SchedulingConfigurer {

  //所有的定时任务都放在一个线程池中，定时任务启动时使用不同都线程。
  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    //设定一个长度10的定时任务线程池
    taskRegistrar.setScheduler(Executors.newScheduledThreadPool(4));
  }
}
