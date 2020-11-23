package com.sutpc.data.send.bus.gps.tencent.client;

import com.sutpc.data.send.bus.gps.tencent.cache.DataCache;
import com.sutpc.data.send.bus.gps.tencent.codec.GpsDataEncoder;
import com.sutpc.data.send.bus.gps.tencent.listening.AbstractConnectionWatchdog;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.HashedWheelTimer;
import java.util.List;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class Client2 {

  @Autowired
  @Qualifier("clientBootstrap2")
  private Bootstrap bootstrap;

  @Value("#{'${tcp.connection}'.split(',')}")
  private List<String> connection;

  private ChannelFuture future;


  private void boostrap() throws Exception {
    String[] c2 = connection.get(1).split(":");
    log.info("address:{},port:{}", c2[0], Integer.valueOf(c2[1]));
    DataCache.getInstance().addNeedSendAddress(c2[0] + ":" + c2[1]);
    start(c2[0], Integer.valueOf(c2[1]));
  }

  /**
   * .
   *
   * @param address ip
   * @param port    端口
   * @throws Exception 异常
   */
  public void start(String address, int port) throws Exception {

    HashedWheelTimer timer = new HashedWheelTimer();
    log.info("start client:{} at port:{}", address, port);

    final AbstractConnectionWatchdog watchdog = new AbstractConnectionWatchdog(bootstrap, timer,
        port, address, true) {

      @Override
      public ChannelHandler[] handlers() {
        return new ChannelHandler[]{
            this,
            new IdleStateHandler(70, 20, 70, TimeUnit.SECONDS),
            new GpsDataEncoder(),
            new ClientHandler()
        };
      }
    };

    try {
      bootstrap.handler(new ChannelInitializer<Channel>() {
        //初始化channel
        @Override
        protected void initChannel(Channel ch) {
          ch.pipeline().addLast(watchdog.handlers());
        }
      });

      future = bootstrap.connect(address, port);
      future.sync();
    } catch (Throwable t) {
      log.info("connect fails method {}", t.getMessage());
      if (future != null) {
        timer.newTimeout(watchdog, 3, TimeUnit.SECONDS);
      }
    }

  }

  /**
   * .
   */
  @PreDestroy
  public void stop() {
    try {
      future.channel().closeFuture().sync();
    } catch (InterruptedException e) {
      log.info("stop method:{}", e.getMessage());
    }
  }
}
