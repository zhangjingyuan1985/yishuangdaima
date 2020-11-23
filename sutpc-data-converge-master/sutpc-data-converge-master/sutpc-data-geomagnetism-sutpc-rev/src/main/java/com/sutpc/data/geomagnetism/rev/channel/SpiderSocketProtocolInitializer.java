package com.sutpc.data.geomagnetism.rev.channel;

import com.sutpc.data.geomagnetism.rev.codec.SpiderDecoder;
import com.sutpc.data.geomagnetism.rev.codec.SpiderEncoder;
import com.sutpc.data.geomagnetism.rev.handle.SpiderServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import java.util.concurrent.TimeUnit;

/**
 * Description:SpiderSocketProtocolInitializer.
 *
 * @author zhy
 * @date 2019/7/31
 */
public class SpiderSocketProtocolInitializer extends ChannelInitializer<SocketChannel> {


  @Override
  protected void initChannel(SocketChannel ch) {
    ChannelPipeline pipeline = ch.pipeline();

    //心跳
    pipeline.addLast(new IdleStateHandler(60, 0, 0, TimeUnit.SECONDS));
    pipeline.addLast(new SpiderEncoder());
    pipeline.addLast(new SpiderDecoder());
    pipeline.addLast(new SpiderServerHandler());
  }
}
