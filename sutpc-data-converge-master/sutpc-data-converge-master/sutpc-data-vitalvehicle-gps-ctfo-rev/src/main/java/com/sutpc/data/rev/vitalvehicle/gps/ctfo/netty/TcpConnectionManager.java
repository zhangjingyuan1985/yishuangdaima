package com.sutpc.data.rev.vitalvehicle.gps.ctfo.netty;

import io.netty.channel.Channel;
import io.netty.util.AttributeKey;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

@Slf4j
public final class TcpConnectionManager {

  /**
   * channel的id.
   */
  private final String channelIdKey = "channelId";
  /**
   * 认证的超时时间.
   */
  private final Integer authTimeoutInterval = 30;
  /**
   * 认证的key.
   */
  private final String authKey = "auth";
  /**
   * 内部的id.
   */
  private final String innerIdKey = "innerId";

  private TcpConnectionManager() {
  }

  /**
   * 单例--Holder模式.
   */
  private static class SingletonHolder {

    private static TcpConnectionManager instance = new TcpConnectionManager();
  }

  public static TcpConnectionManager getInstance() {
    return SingletonHolder.instance;
  }

  /**
   * .
   *
   * @param channel 通道
   */
  public void channelActive(Channel channel) {
    String id = StatefulChannelGroup.getInstance().addConnected(channel);
    channel.attr(AttributeKey.valueOf(channelIdKey)).set(id);
    ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1,
        new BasicThreadFactory.Builder().namingPattern("auth-timeout-pool-%d").daemon(false)
            .build());
    AuthTimeoutCheckTask authTimeoutCheckTask = new AuthTimeoutCheckTask(id,
        scheduledExecutorService, authTimeoutInterval);
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
      Object channelIdObject = channel.attr(AttributeKey.valueOf(channelIdKey)).get();
      String channelId = null;
      if (channelIdObject != null) {
        channelId = channelIdObject.toString();
        channel.attr(AttributeKey.valueOf(channelIdKey)).remove();
        log.info("channel id of channel attribute:{}" + channelId);
      }

      AuthTimeoutCheckTask authTimeoutCheckTask = (AuthTimeoutCheckTask) channel
          .attr(AttributeKey.valueOf(authKey)).get();
      if (authTimeoutCheckTask != null) {
        authTimeoutCheckTask.getTimer().shutdownNow();
        channel.attr(AttributeKey.valueOf(authKey)).remove();
        log.info("auth 's channelid of channel attribute:{}" + authTimeoutCheckTask.getChannelId());
      }

      //删除Channel组中的记录
      if (!StringUtils.isEmpty(channelId)) {
        StatefulChannelGroup.getInstance().removeConnected(channelId);
        StatefulChannelGroup.getInstance().removeAuthed(channelId);
      }
      //channel是否是活跃的
      if (channel.isActive() || channel.isOpen()) {
        channel.close();
      }
    }
  }

  /**
   * 连接认证.
   *
   * @param channelId 通道ID
   * @param innerId 内部ID
   */
  public void channelAuthed(String channelId, String innerId) {
    Channel channel = StatefulChannelGroup.getInstance().getConnected(channelId);
    StatefulChannelGroup.getInstance().removeConnected(channelId);

    AuthTimeoutCheckTask authTimeoutCheckTask = (AuthTimeoutCheckTask) channel
        .attr(AttributeKey.valueOf(authKey)).get();
    authTimeoutCheckTask.getTimer().shutdownNow();

    channel.attr(AttributeKey.valueOf(authKey)).remove();
    channel.attr(AttributeKey.valueOf(innerIdKey)).set(innerId);

    StatefulChannelGroup.getInstance().addAuthed(innerId, channelId);
  }

  private class AuthTimeoutCheckTask implements Runnable {

    /**
     * 延迟的秒数.
     */
    private int delaySeconds;
    /**
     * channel的id.
     */
    private String channelId;
    /**
     * 线程调度任务.
     */
    private ScheduledExecutorService scheduledExecutorService;

    public AuthTimeoutCheckTask(String channelId, ScheduledExecutorService scheduledExecutorService,
        int delaySeconds) {
      this.channelId = channelId;
      this.scheduledExecutorService = scheduledExecutorService;
      this.delaySeconds = delaySeconds;
      this.scheduledExecutorService.schedule(this, this.delaySeconds, TimeUnit.SECONDS);
    }

    @Override
    public void run() {
      Channel channel = StatefulChannelGroup.getInstance().getConnected(channelId);
      log.info("AuthTimeout, close channel, channelId is {},remote address is {} ", channelId,
          channel.remoteAddress());
      try {
        channelInActive(channel);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }


    public String getChannelId() {
      return channelId;
    }

    public void setChannelId(String channelId) {
      this.channelId = channelId;
    }

    public ScheduledExecutorService getTimer() {
      return scheduledExecutorService;
    }
  }
}
