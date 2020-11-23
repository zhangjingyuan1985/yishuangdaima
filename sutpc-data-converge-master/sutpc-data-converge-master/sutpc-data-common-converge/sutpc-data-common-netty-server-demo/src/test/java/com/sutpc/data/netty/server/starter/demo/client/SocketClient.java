package com.sutpc.data.netty.server.starter.demo.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class SocketClient {

  /**
   * .
   */
  public static void main(String[] args) {
    EventLoopGroup eventLoopGroup = new NioEventLoopGroup();
    try {
      Bootstrap bootstrap = new Bootstrap();
      bootstrap.group(eventLoopGroup).channel(NioSocketChannel.class)
          .remoteAddress("127.0.0.1", 4031)
          .handler(new ClientSocketProtocolInitalizer());
      ChannelFuture channelFuture = bootstrap.connect().sync();

      for (int i = 0; i < 10; i++) {
        channelFuture.channel().writeAndFlush("来自于客户端的问候" + i);
      }
      channelFuture.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      eventLoopGroup.shutdownGracefully();
    }
  }
}
