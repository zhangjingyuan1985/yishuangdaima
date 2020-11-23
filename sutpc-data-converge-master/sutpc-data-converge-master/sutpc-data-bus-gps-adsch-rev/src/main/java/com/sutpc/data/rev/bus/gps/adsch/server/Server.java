package com.sutpc.data.rev.bus.gps.adsch.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;
import javax.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * .
 *
 * @Auth smilesnake minyikun
 * @Create 2019/8/7 17:48
 */
@Slf4j
@Component
public class Server {

  @Autowired
  @Qualifier("serverBootstrap")
  private ServerBootstrap bootstrap;
  @Value("${tcp.port}")
  private Integer port;
  private ChannelFuture future;

  private void start() throws InterruptedException {
    bootstrap.handler(new LoggingHandler(LogLevel.INFO))
        .childHandler(new ChannelInitializer() {
          @Override
          protected void initChannel(Channel ch) throws Exception {
            ChannelPipeline pipeline = ch.pipeline();
            pipeline.addLast(new IdleStateHandler(5, 0, 0, TimeUnit.SECONDS));
            //ch.pipeline().addLast(idleStateTrigger);
            pipeline.addLast(new ChannelHandler[]{new LineBasedFrameDecoder(Integer.MAX_VALUE)});
            pipeline.addLast(new ChannelHandler[]{new StringDecoder(Charset.forName("UTF-8"))});
            pipeline.addLast(new ServerHandler());
          }
        });
    future = bootstrap.bind(port).sync();
  }

  @PreDestroy
  public void stop() throws Exception {
    future.channel().closeFuture().sync();
  }
}
