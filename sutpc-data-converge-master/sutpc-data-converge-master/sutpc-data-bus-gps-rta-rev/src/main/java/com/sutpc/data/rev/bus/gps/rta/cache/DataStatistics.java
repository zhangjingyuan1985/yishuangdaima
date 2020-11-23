package com.sutpc.data.rev.bus.gps.rta.cache;

import java.util.concurrent.atomic.AtomicLong;
import lombok.Data;

/**
 * 数据统计.
 *
 * @Auth smilesnake minyikun
 * @Create 2019/8/20 18:28
 */
@Data
public class DataStatistics {

  /**
   * 总数据量.
   */
  private AtomicLong dataTotal = new AtomicLong(0);
  /**
   * 有效的数据量.
   */
  private AtomicLong availableTotal = new AtomicLong(0);
  /**
   * 发送的数据量.
   */
  private AtomicLong sendTotal = new AtomicLong(0);
  /**
   * 空数据量.
   */
  private AtomicLong nullTotal = new AtomicLong(0);
  /**
   * 错误的数据.
   */
  private AtomicLong errorTotal = new AtomicLong(0);


  private DataStatistics() {
  }

  private static class SingletonHolder {

    private static final DataStatistics INSTANCE = new DataStatistics();
  }

  public static DataStatistics getInstance() {
    return DataStatistics.SingletonHolder.INSTANCE;
  }
}
