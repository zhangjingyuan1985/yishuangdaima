package com.sutpc.data.rev.vitalvehicle.gps.ctfo.netty;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier("serverSocketProtocolInitializer")
public class ServerSocketProtocolInitalizer extends ChannelInitializer<SocketChannel> {

  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();

    //心跳
    pipeline.addLast(new IdleStateHandler(30, 30, 60, TimeUnit.MINUTES));
    pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
    //以换行符为结束标记
    pipeline.addLast(new LineBasedFrameDecoder(Integer.MAX_VALUE));
    pipeline.addLast(new StringDecoder());
    pipeline.addLast(new ServerHandler());
    pipeline.addLast(new LoggingHandler());
  }
}