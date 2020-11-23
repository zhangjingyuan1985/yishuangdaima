package com.sutpc.data.rev.vitalvehicle.gps.ctfo.cache;

import java.time.LocalDateTime;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.Data;

/**
 * 信息缓存.
 * @Auth smilesnake minyikun
 * @Create 2020/1/8 17:50 
 */
@Data
public class MessageCache {

  /**
   * .
   */
  private static class SingletonHolder {

    private static final MessageCache INSTANCE = new MessageCache();
  }


  /**
   * 得到当前对象.
   * @return
   */
  public static MessageCache getInstance() {
    return MessageCache.SingletonHolder.INSTANCE;
  }

  private MessageCache() {
  }

  /**
   * 范围.
   */
  private final int[] range = {5, 15, 30, 60};
  /**
   * 当前范围的key.
   */
  private final AtomicInteger index = new AtomicInteger(0);
  /**
   * 接收的时间.
   */
  private LocalDateTime revTime = LocalDateTime.now();

  public void reset() {
    this.index.set(0);
    this.revTime = LocalDateTime.now();
  }
}
