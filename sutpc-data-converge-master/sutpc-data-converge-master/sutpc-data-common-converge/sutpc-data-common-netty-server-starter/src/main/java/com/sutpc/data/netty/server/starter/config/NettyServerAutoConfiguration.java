package com.sutpc.data.netty.server.starter.config;

import com.sutpc.data.netty.server.starter.netty.NettyServerBootstrap;
import com.sutpc.data.netty.server.starter.netty.initializer.ServerSocketProtocolInitalizer;
import com.sutpc.data.netty.server.starter.properties.NettyServerProperties;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;


/**
 * 请修改描述.
 *
 * @filename : NettyServerAutoConfiguration
 * @creater : tangshaofeng
 * @updater : tangshaofeng
 * @createtime : 2019-09-19 16:09
 * @updateTime : 2019-09-19 16:09
 */
@Configuration
@EnableConfigurationProperties(NettyServerProperties.class)
public class NettyServerAutoConfiguration {

  @Resource
  private NettyServerProperties nettyServerProperties;

  @Autowired
  private ChannelInitializer channelInitializer;

  @Bean
  @ConditionalOnMissingBean
  @Scope("singleton")
  public ChannelInitializer channelInitializer() {
    return new ServerSocketProtocolInitalizer();
  }

  @Bean
  public NettyServerBootstrap init() {
    ServerBootstrap serverBootstrap = new ServerBootstrap();
    serverBootstrap.group(new NioEventLoopGroup(nettyServerProperties.getBossThreads()),
        new NioEventLoopGroup(nettyServerProperties.getWorkerThreads()));
    serverBootstrap.channel(NioServerSocketChannel.class);
    serverBootstrap
        .option(ChannelOption.SO_BACKLOG, nettyServerProperties.getParentOptionSoBacklog());
    serverBootstrap
        .option(ChannelOption.TCP_NODELAY, nettyServerProperties.getParentOptionTcpNodelay());
    serverBootstrap
        .option(ChannelOption.SO_LINGER, nettyServerProperties.getParentOptionSoLinger());
    serverBootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS,
        nettyServerProperties.getParentOptionConnectTimeoutMills());
    serverBootstrap
        .option(ChannelOption.SO_KEEPALIVE, nettyServerProperties.getParentOptionSoKeepAlive());
    serverBootstrap
        .option(ChannelOption.SO_RCVBUF, nettyServerProperties.getParentOptionSoRcvBuf());
    serverBootstrap
        .option(ChannelOption.SO_SNDBUF, nettyServerProperties.getParentOptionSoSndBuf());
    serverBootstrap
        .option(ChannelOption.SO_REUSEADDR, nettyServerProperties.getParentOptionSoReuseAddr());
    serverBootstrap
        .childOption(ChannelOption.TCP_NODELAY, nettyServerProperties.getChildOptionTcpNodelay());
    serverBootstrap
        .childOption(ChannelOption.SO_LINGER, nettyServerProperties.getChildOptionSoLinger());
    serverBootstrap.childOption(ChannelOption.CONNECT_TIMEOUT_MILLIS,
        nettyServerProperties.getChildOptionConnectTimeoutMills());
    serverBootstrap
        .childOption(ChannelOption.SO_KEEPALIVE, nettyServerProperties.getChildOptionKeepAlive());
    serverBootstrap
        .childOption(ChannelOption.SO_RCVBUF, nettyServerProperties.getChildOptionSoRcvBuf());
    serverBootstrap
        .childOption(ChannelOption.SO_SNDBUF, nettyServerProperties.getChildOptionSoSndBuf());
    serverBootstrap.childHandler(channelInitializer);
    NettyServerBootstrap nettyServerBootstrap = new NettyServerBootstrap();
    nettyServerBootstrap.setServerBootstrap(serverBootstrap);
    nettyServerBootstrap.setPort(nettyServerProperties.getPort());
    return nettyServerBootstrap;
  }
}
