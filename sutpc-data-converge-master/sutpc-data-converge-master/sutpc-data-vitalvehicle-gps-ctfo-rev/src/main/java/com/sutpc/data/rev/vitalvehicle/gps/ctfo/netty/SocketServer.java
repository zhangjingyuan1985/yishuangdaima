package com.sutpc.data.rev.vitalvehicle.gps.ctfo.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import java.net.InetSocketAddress;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SocketServer {
  @Autowired
  @Qualifier("serverBootstrap")
  private ServerBootstrap bootstrap;

  @Autowired
  @Qualifier("tcpSocketAddress")
  private InetSocketAddress tcpPort;

  private ChannelFuture serverChannelFuture;

  @PostConstruct
  public void start() throws Exception {
    System.out.println("Starting server at " + tcpPort);
    serverChannelFuture = bootstrap.bind(tcpPort).sync();
  }

  @PreDestroy
  public void stop() throws Exception {
    serverChannelFuture.channel().closeFuture().sync();
  }
}
