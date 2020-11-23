package com.sutpc.data.file.ic.card.sutpc.cache;

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
   * 解析后消费的数据量.
   */
  private AtomicLong consumerDataTotal = new AtomicLong(0);
  /**
   * 解析错误的数据量.
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
