package com.sutpc.data.rev.vitalvehicle.gps.ctfo.netty;

import io.netty.channel.Channel;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StatefulChannelGroup {

  /*
   * 已经连接但尚未认证的channel与id的映射map
   */
  private ConcurrentMap<String, Channel> connectedChannel;
  /*
   * 已经连接且认证的channel与id的映射map <userId,channelId>
   */
  private ConcurrentMap<String, String> authedChannel;

  private StatefulChannelGroup() {
    connectedChannel = new ConcurrentHashMap<>();
    authedChannel = new ConcurrentHashMap<>();
  }

  private static class SingletonHolder {

    private static final StatefulChannelGroup INSTANCE = new StatefulChannelGroup();
  }

  public static StatefulChannelGroup getInstance() {
    return SingletonHolder.INSTANCE;
  }

  /**
   * .
   *
   * @param channel 通道
   */
  public String addConnected(Channel channel) {
    boolean isIdNotExist = true;
    String channelId = null;
    while (isIdNotExist) {
      channelId = UUID.randomUUID().toString().replace("-", "");
      if (connectedChannel.putIfAbsent(channelId, channel) == null) {
        isIdNotExist = false;
      }
    }
    return channelId;
  }

  public Channel removeConnected(String channelId) {
    return connectedChannel.remove(channelId);
  }

  public Channel getConnected(String id) {
    return connectedChannel.get(id);
  }

  public String addAuthed(String id, String channleId) {
    return authedChannel.putIfAbsent(id, channleId);
  }

  public String removeAuthed(String id) {
    return authedChannel.remove(id);
  }

  public String getAuthed(String id) {
    return authedChannel.get(id);
  }

  public ConcurrentMap<String, Channel> getCollectMap() {
    return connectedChannel;
  }

}
