package com.sutpc.data.netty.server.starter.netty.manager;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

@Slf4j
public final class ConnectionManager {

  /**
   * 认证的超时时间.
   */
  private final Integer authTimeoutInterval = 30;
  /**
   * 认证的key.
   */
  private final String authKey = "auth";

  private ConnectionManager() {
  }

  /**
   * 单例--Holder模式.
   */
  private static class SingletonHolder {

    private static ConnectionManager instance = new ConnectionManager();
  }

  public static ConnectionManager getInstance() {
    return SingletonHolder.instance;
  }

  /**
   * .
   *
   * @param channel 通道
   */
  public void channelActive(Channel channel) {
    ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
        new BasicThreadFactory.Builder().namingPattern("auth-timeout-pool-%d").daemon(false)
            .build());
    AuthTimeoutCheckTask authTimeoutCheckTask = new AuthTimeoutCheckTask(channel,
        scheduledExecutorService,
        authTimeoutInterval);
    channel.attr(AttributeKey.valueOf(authKey)).set(authTimeoutCheckTask);
  }

  /**
   * .
   *
   * @param channel 通道
   * @throws Exception 异常
   */
  public void channelInActive(Channel channel) throws Exception {
    if (channel != null) {
      synchronized (channel) {
        AuthTimeoutCheckTask authTimeoutCheckTask = (AuthTimeoutCheckTask) channel
            .attr(AttributeKey.valueOf(authKey)).get();
        if (authTimeoutCheckTask != null) {
          authTimeoutCheckTask.getTimer().shutdownNow();
          channel.attr(AttributeKey.valueOf(authKey)).getAndSet(null);
        }

        //删除Channel组中的记录
        StatefulChannelGroup.getInstance().removeAuthedByValue(channel);
        //channel是否是活跃的
        if (channel.isActive() || channel.isOpen()) {
          channel.close();
        }
      }
    }
  }

  /**
   * .
   *
   * @param channel 通道
   * @param innerId 内部ID
   */
  public void channelAuthed(Channel channel, String innerId) {
    synchronized (channel) {
      AuthTimeoutCheckTask authTimeoutCheckTask = (AuthTimeoutCheckTask) channel
          .attr(AttributeKey.valueOf(authKey)).get();
      authTimeoutCheckTask.getTimer().shutdownNow();

      channel.attr(AttributeKey.valueOf(authKey)).getAndSet(null);
    }

    StatefulChannelGroup.getInstance().addAuthed(innerId, channel);
  }

  private class AuthTimeoutCheckTask implements Runnable {

    /**
     * 延迟的秒数.
     */
    private int delaySeconds;
    private Channel channel;
    /**
     * 线程调度任务.
     */
    private ScheduledExecutorService scheduledExecutorService;

    public AuthTimeoutCheckTask(Channel channel, ScheduledExecutorService scheduledExecutorService,
        int delaySeconds) {
      this.channel = channel;
      this.scheduledExecutorService = scheduledExecutorService;
      this.delaySeconds = delaySeconds;
      scheduledExecutorService.schedule(this, this.delaySeconds, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
      log.info("AuthTimeout, close channel, ,remote address is {} ", channel.remoteAddress());
      try {
        channelInActive(channel);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }


    public Channel getChannel() {
      return channel;
    }


    public ScheduledExecutorService getTimer() {
      return scheduledExecutorService;
    }
  }
}
