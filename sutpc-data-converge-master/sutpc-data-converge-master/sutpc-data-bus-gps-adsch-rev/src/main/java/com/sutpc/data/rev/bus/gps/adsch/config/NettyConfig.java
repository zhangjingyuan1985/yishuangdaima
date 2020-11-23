package com.sutpc.data.rev.bus.gps.adsch.config;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class NettyConfig {

  @Value("${worker.thread.count}")
  private int workerCount;
  @Value("${boss.thread.count}")
  private int bossCount;


  @Bean(name = "clientBootstrap")
  public Bootstrap bootstrap() {
    return bootstrapConfig(workerGroup(null));
  }

  @Bean(name = "serverBootstrap")
  public ServerBootstrap serverBootstrap() {
    return serverConfig(bossCount, null);
  }

  private Bootstrap bootstrapConfig(NioEventLoopGroup workerGroup) {
    Bootstrap b = new Bootstrap();
    b.option(ChannelOption.TCP_NODELAY, true);
    b.option(ChannelOption.SO_LINGER, -1);
    b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
    b.option(ChannelOption.SO_KEEPALIVE, true);
    b.option(ChannelOption.SO_RCVBUF, 8192);
    b.option(ChannelOption.SO_SNDBUF, 8192);
    b.group(workerGroup)
        .channel(NioSocketChannel.class);
    return b;
  }

  private ServerBootstrap serverConfig(Integer bossCount, Integer workerCount) {
    ServerBootstrap b = new ServerBootstrap();
    b.group(bossGroup(bossCount), workerGroup(null))
        .channel(NioServerSocketChannel.class);
    b.option(ChannelOption.TCP_NODELAY, true);
    b.option(ChannelOption.SO_LINGER, -1);
    b.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000);
    b.option(ChannelOption.SO_KEEPALIVE, true);
    b.option(ChannelOption.SO_RCVBUF, 8192);
    b.option(ChannelOption.SO_SNDBUF, 8192);
    b.option(ChannelOption.SO_BACKLOG, 128);
    return b;
  }

  private NioEventLoopGroup workerGroup(Integer workerCount) {
    if (workerCount == null) {
      return new NioEventLoopGroup();
    }
    return new NioEventLoopGroup(workerCount);
  }

  private NioEventLoopGroup bossGroup(Integer bossCount) {
    if (bossCount == null) {
      return new NioEventLoopGroup();
    }
    return new NioEventLoopGroup(bossCount);
  }

}
