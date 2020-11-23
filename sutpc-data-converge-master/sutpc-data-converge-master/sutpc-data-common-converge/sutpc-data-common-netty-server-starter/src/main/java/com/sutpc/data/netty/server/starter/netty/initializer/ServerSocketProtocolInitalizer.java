package com.sutpc.data.netty.server.starter.netty.initializer;

import com.sutpc.data.netty.server.starter.netty.handler.ServerHandler;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

@Sharable
public class ServerSocketProtocolInitalizer extends ChannelInitializer<SocketChannel> {


  @Override
  protected void initChannel(SocketChannel ch) throws Exception {
    ChannelPipeline pipeline = ch.pipeline();

    pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
    pipeline.addLast(new StringDecoder());
    pipeline.addLast(new ServerHandler());
  }
}