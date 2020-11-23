package com.sutpc.data.rev.vitalvehicle.gps.ctfo.cache;

import lombok.Data;

/**
 * 消息计数器.
 * @Auth smilesnake minyikun
 * @Create 2020/1/8 14:55 
 */
@Data
public class MessageCounter {

  /**
   * .
   */
  private MessageCounter() {

  }

  /**
   * .
   */
  private static class SingletonHolder {

    private static final MessageCounter INSTANCE = new MessageCounter();
  }


  /**
   * 得到当前对象.
   * @return
   */
  public static MessageCounter getInstance() {
    return MessageCounter.SingletonHolder.INSTANCE;
  }

  private MessageCache cache = MessageCache.getInstance();
}
