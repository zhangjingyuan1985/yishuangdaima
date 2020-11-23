package com.sutpc.data.rev.vitalvehicle.gps.ctfo.client;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;
import java.util.concurrent.TimeUnit;

public class ClientSocketProtocolInitalizer extends ChannelInitializer<SocketChannel> {

  @Override
  protected void initChannel(SocketChannel ch) {
    ChannelPipeline pipeline = ch.pipeline();

    pipeline.addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));
    //以换行符为结束标记
    pipeline.addLast("lineBasedFrameDecoder", new LineBasedFrameDecoder(Integer.MAX_VALUE));
    pipeline.addLast("stringDecoder", new StringDecoder());
    pipeline.addLast("clientHandler", new ClientHandler());
  }


}
