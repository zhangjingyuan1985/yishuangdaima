package com.sutpc.data.send.bus.gps.tencent.listening;

import com.sutpc.data.send.bus.gps.tencent.cache.DataCache;
import lombok.extern.slf4j.Slf4j;

/**
 * .
 * @Auth smilesnake minyikun.
 * @Create 2019/8/2 15:13
 */
@Slf4j
public class AnalysisTask {

  /**
   * .
   */
  public void analysis() {
    log.info("total:{},loss to quere {}, loss to net {}",
        DataCache.getInstance().getTotal().getAndSet(0), DataCache.getInstance()
            .getLossCount().getAndSet(0), DataCache.getInstance().getNetCount().getAndSet(0));
  }
}

