package com.sutpc.data.netty.server.starter.netty.manager;

import io.netty.channel.Channel;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StatefulChannelGroup {

  private ConcurrentMap<String, Channel> authedChannel;

  private StatefulChannelGroup() {
    authedChannel = new ConcurrentHashMap<>();
  }

  private static class SingletonHolder {

    private static final StatefulChannelGroup INSTANCE = new StatefulChannelGroup();
  }

  public static StatefulChannelGroup getInstance() {
    return SingletonHolder.INSTANCE;
  }


  public Channel addAuthed(String id, Channel channel) {
    return authedChannel.putIfAbsent(id, channel);
  }

  public Channel removeAuthed(String id) {
    return authedChannel.remove(id);
  }

  public Channel getAuthed(String id) {
    return authedChannel.get(id);
  }

  /**
   *  .
   * @param channel 通道
   */
  public void removeAuthedByValue(Channel channel) {
    synchronized (authedChannel) {
      Set<String> keySet = authedChannel.keySet();
      Iterator<Map.Entry<String, Channel>> iterator = authedChannel.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry<String, Channel> entry = iterator.next();
        if (entry.getValue().equals(channel)) {
          iterator.remove();
          break;
        }
      }
    }
  }

  public void sendBufferById(String id, Object object) {
    authedChannel.get(id).writeAndFlush(object);
  }


}
