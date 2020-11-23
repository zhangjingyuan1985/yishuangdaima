package com.sutpc.data.send.bus.gps.tencent.config;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
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


  @Bean(name = "clientBootstrap1")
  public Bootstrap bootstrap() {
    return bootstrapConfig(workerGroup());
  }

  @Bean(name = "clientBootstrap2")
  public Bootstrap bootstrap2() {
    return bootstrapConfig(workerGroup());
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

  /**
   *  .
   * @return
   */
  //@Bean(name = "workerGroup", destroyMethod = "shutdownGracefully")
  public NioEventLoopGroup workerGroup() {
    log.info("availableProcessors is {}", Runtime.getRuntime().availableProcessors());
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    return new NioEventLoopGroup(workerCount);
  }

}
