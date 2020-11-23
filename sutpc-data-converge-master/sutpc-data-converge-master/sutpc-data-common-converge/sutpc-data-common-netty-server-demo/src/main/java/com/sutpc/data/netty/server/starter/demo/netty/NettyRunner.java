package com.sutpc.data.netty.server.starter.demo.netty;
/**
 * @description:
 * @author:tangshaofeng
 * @createtime:2019/7/17 14:33
 */

import com.sutpc.data.netty.server.starter.netty.NettyServerBootstrap;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 请修改描述.
 *
 * @filename : NettyRunner
 * @creater : tangshaofeng
 * @updater : tangshaofeng
 * @createtime : 2019-09-19 16:16
 * @updateTime : 2019-09-19 16:16
 */
@Component
public class NettyRunner {

  @Autowired
  NettyServerBootstrap nettyServerBootstrap;

  /**
   *  .
   */
  @PostConstruct
  public void runNettyServer() {
    System.out.println("run netty server");
    try {
      nettyServerBootstrap.startNeetyServer();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  /**
   * .
   */
  @PreDestroy
  public void shutdownNettyServer() {
    System.out.println("shutdown netty server");
    try {
      nettyServerBootstrap.shutdownNettyServer();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}
