package com.sutpc.data.rev.vitalvehicle.gps.ctfo.config;

import com.sutpc.data.rev.vitalvehicle.gps.ctfo.netty.ServerSocketProtocolInitalizer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 请修改描述.
 *
 * @filename : NettyConfig
 * @creater : tangshaofeng
 * @updater : tangshaofeng
 * @createtime : 2019-09-19 15:23
 * @updateTime : 2019-09-19 15:23
 */
@Configuration
public class NettyConfig {

  @Value("${boss.thread.count}")
  private int bossCount;

  @Value("${worker.thread.count}")
  private int workerCount;

  @Value("${tcp.port}")
  private int tcpPort;

  @Value("${so.keepalive}")
  private boolean keepAlive;

  @Value("${so.backlog}")
  private int backlog;

  @Autowired
  @Qualifier("serverSocketProtocolInitializer")
  private ServerSocketProtocolInitalizer serverProtocolInitalizer;

  /**
   * bootstrap配置.
   * @return
   */
  @SuppressWarnings("unchecked")
  @Bean(name = "serverBootstrap")
  public ServerBootstrap bootstrap() {
    ServerBootstrap b = new ServerBootstrap();
    b.group(bossGroup(), workerGroup())
        .channel(NioServerSocketChannel.class)
        .handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(serverProtocolInitalizer);
    Map<ChannelOption<?>, Object> tcpChannelOptions = tcpChannelOptions();
    Set<ChannelOption<?>> keySet = tcpChannelOptions.keySet();
    for (@SuppressWarnings("rawtypes")
        ChannelOption option : keySet) {
      b.option(option, tcpChannelOptions.get(option));
    }
    return b;
  }

  @Bean(name = "bossGroup", destroyMethod = "shutdownGracefully")
  public NioEventLoopGroup bossGroup() {
    return new NioEventLoopGroup(bossCount);
  }

  @Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
  public NioEventLoopGroup workerGroup() {
    return new NioEventLoopGroup(workerCount);
  }

  @Bean(name = "tcpSocketAddress")
  public InetSocketAddress tcpPort() {
    return new InetSocketAddress(tcpPort);
  }

  /**
   * 修改channel选项.
   * @return
   */
  @Bean(name = "tcpChannelOptions")
  public Map<ChannelOption<?>, Object> tcpChannelOptions() {
    Map<ChannelOption<?>, Object> options = new HashMap<ChannelOption<?>, Object>();
    options.put(ChannelOption.SO_KEEPALIVE, keepAlive);
    options.put(ChannelOption.SO_BACKLOG, backlog);
    return options;
  }
}
