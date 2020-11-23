package com.sutpc.data.rev.bus.gps.adsch.client;

import com.sutpc.data.rev.bus.gps.adsch.codec.SatelliteDecoder;
import com.sutpc.data.rev.bus.gps.adsch.listening.AbstractConnectionWatchdog;
import com.sutpc.data.rev.bus.gps.adsch.listening.ConnectorIdleStateTrigger;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.util.HashedWheelTimer;

import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Client {

  @Autowired
  @Qualifier("clientBootstrap")
  private Bootstrap bootstrap;


  @Value("#{'${tcp.connection}'.split(',')}")
  private List<String> connection;

  private ChannelFuture future;
  private final ConnectorIdleStateTrigger idleStateTrigger = new ConnectorIdleStateTrigger();

  @PostConstruct
  private void boostrap() throws Exception {
    String[] c1 = connection.get(0).split(":");
    log.info("address:{},port:{}", c1[0], Integer.valueOf(c1[1]));
    start(c1[0], Integer.valueOf(c1[1]));
  }

  /**
   * 启动入口.
   *
   * @param address 监听ip
   * @param port    监听端口
   */
  public void start(String address, int port) {

    HashedWheelTimer timer = new HashedWheelTimer();
    log.info("start server:{} at port:{}", address, port);

    final AbstractConnectionWatchdog watchdog = new AbstractConnectionWatchdog(bootstrap, timer,
            port, address,
            true) {
      @Override
      public ChannelHandler[] handlers() {
        return new ChannelHandler[]{
                this,
                //new IdleStateHandler(70, 20, 70, TimeUnit.SECONDS),
                //new BusGpsDecoder(),
                new LengthFieldBasedFrameDecoder(1 << 16, 4, 2, 2, 0),
                new SatelliteDecoder(),
                new ClientHandler()
        };
      }
    };

    try {
      synchronized (bootstrap) {
        bootstrap.handler(new ChannelInitializer<Channel>() {
          //初始化channel
          @Override
          protected void initChannel(Channel ch) {
            ch.pipeline().addLast(watchdog.handlers());
          }
        });
        future = bootstrap.connect(address, port);
      }
      future.sync();
    } catch (Throwable t) {
      log.info("connect fails {}", t.getMessage());
      if (future != null) {
        timer.newTimeout(watchdog, 15, TimeUnit.SECONDS);
      }
    }

  }

  @PreDestroy
  public void stop() throws Exception {
    future.channel().closeFuture().sync();
  }
}
