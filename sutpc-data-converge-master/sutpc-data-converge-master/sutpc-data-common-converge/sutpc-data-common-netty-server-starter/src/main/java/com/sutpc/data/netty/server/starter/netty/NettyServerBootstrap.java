package com.sutpc.data.netty.server.starter.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import lombok.Data;


/**
 * 请修改描述.
 *
 * @filename : NettyServerBootstrap
 * @creater : tangshaofeng
 * @updater : tangshaofeng
 * @createtime : 2019-09-19 16:14
 * @updateTime : 2019-09-19 16:14
 */
@Data
public class NettyServerBootstrap {

  ServerBootstrap serverBootstrap;
  Integer port;
  Channel serverChannel;
  EventLoopGroup bossEventLoopGroup;
  EventLoopGroup workerEventLoopGroup;

  public void startNeetyServer() throws InterruptedException {
    ChannelFuture future = serverBootstrap.bind(port == null ? 4031 : port).sync();
    serverChannel = future.channel();
  }

  /**
   *  .
   * @throws InterruptedException 异常
   */
  public void shutdownNettyServer() throws InterruptedException {
    serverChannel.close().sync();
    bossEventLoopGroup.shutdownGracefully().await();
    workerEventLoopGroup.shutdownGracefully().await();
  }
}
