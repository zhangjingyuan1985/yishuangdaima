package com.sutpc.data.file.ic.card.sutpc.task;

import com.sutpc.data.file.ic.card.sutpc.cache.DataStatistics;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 *  .
 * @Auth smilesnake minyikun
 * @Create 2019/8/2 15:13
 */
@Component
@Slf4j
public class AnalysisTask {

  /**
   *  .
   */
  @Scheduled(cron = "0 0/5 * * * ?")
  public void analysis() {
    log.info("-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --  -- -- -- -- -- -- -- -- -- "
        + "-- -- -- -- -- -- -- -- -- -- -- -- -- --");
    log.info("-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --  5 min statistical -- -- -- "
        + "-- -- -- -- -- -- -- -- -- -- -- -- -- --");
    log.info("-- -- -- -- -- -- -- -- -- --  data total:{},send data total:{},error data "
            + "total: {} -- -- -- -- -- -- -- -- -- -- ",
            DataStatistics.getInstance().getDataTotal().getAndSet(0),
            DataStatistics.getInstance().getConsumerDataTotal().getAndSet(0),
            DataStatistics.getInstance().getErrorTotal().getAndSet(0));
    log.info("-- -- -- -- -- -- -- -- -- -- -- -- -- -- -- -- --  -- -- -- -- -- -- -- -- -- "
        + "-- -- -- -- -- -- -- -- -- -- -- -- -- --");
  }
}
