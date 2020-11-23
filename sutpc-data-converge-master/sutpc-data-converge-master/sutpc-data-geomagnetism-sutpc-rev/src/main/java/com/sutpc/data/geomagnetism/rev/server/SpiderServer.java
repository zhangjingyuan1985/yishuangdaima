package com.sutpc.data.geomagnetism.rev.server;

import com.sutpc.data.netty.server.starter.netty.NettyServerBootstrap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Description:Spider netty服务.
 *
 * @author zhy
 * @date 2019/7/31
 */
@Slf4j
@Component
public class SpiderServer {

  private final NettyServerBootstrap nettyServerBootstrap;

  public SpiderServer(
      NettyServerBootstrap nettyServerBootstrap) {
    this.nettyServerBootstrap = nettyServerBootstrap;
  }

  /**
   * 初始化创建.
   */
  @PostConstruct
  public void runNettyServer() {
    log.info("netty server is start");
    try {
      nettyServerBootstrap.startNeetyServer();
    } catch (InterruptedException e) {
      log.error("netty server is error", e);
      Thread.currentThread().interrupt();
    }
  }

  /**
   * 关闭.
   */
  @PreDestroy
  public void shutdownNettyServer() {
    log.info("shutdown netty server");
    try {
      nettyServerBootstrap.shutdownNettyServer();
    } catch (InterruptedException e) {
      log.error("netty server is error", e);
      Thread.currentThread().interrupt();
    }
  }

}