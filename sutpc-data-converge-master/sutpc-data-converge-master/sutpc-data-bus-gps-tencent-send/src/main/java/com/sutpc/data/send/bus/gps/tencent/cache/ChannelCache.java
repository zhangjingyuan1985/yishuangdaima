package com.sutpc.data.send.bus.gps.tencent.cache;

import io.netty.channel.Channel;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ChannelCache {

  private ChannelCache() {
  }

  private List<Channel> channels = new ArrayList<>();

  private static class SingletonHolder {

    private static final ChannelCache INSTANCE = new ChannelCache();
  }

  public static ChannelCache getInstance() {
    return ChannelCache.SingletonHolder.INSTANCE;
  }

  public void put(Channel channel) {
    channels.add(channel);
  }

  public void remove(Channel channel) {
    channels.remove(channel);
  }

  public List<Channel> getChannels() {
    log.info("channel size:{}", channels.size());
    return channels;
  }
}
