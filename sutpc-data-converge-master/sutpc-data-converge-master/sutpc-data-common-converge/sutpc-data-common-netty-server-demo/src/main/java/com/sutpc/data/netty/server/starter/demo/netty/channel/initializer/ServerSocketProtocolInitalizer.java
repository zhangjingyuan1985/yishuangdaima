package com.sutpc.data.netty.server.starter.demo.netty.channel.initializer;

import com.sutpc.data.netty.server.starter.demo.netty.handler.ServerHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@Sharable
/**
 * 请修改描述.
 *
 * @filename : ServerSocketProtocolInitalizer
 * @creater : tangshaofeng
 * @updater : tangshaofeng
 * @createtime : 2019-09-19 16:16
 * @updateTime : 2019-09-19 16:16
 */
public class ServerSocketProtocolInitalizer extends ChannelInitializer<SocketChannel> {

  @Autowired
  private ServerHandler serverHandler;
  @Autowired
  private StringEncoder stringEncoder;
  @Autowired
  private StringDecoder stringDecoder;

  @Bean
  public StringEncoder initEncoder() {
    return new StringEncoder(CharsetUtil.UTF_8);
  }

  @Bean
  StringDecoder initDecoder() {
    return new StringDecoder();
  }

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();

    pipeline.addLast(stringEncoder);
    pipeline.addLast(stringDecoder);
    pipeline.addLast(serverHandler);
  }
}