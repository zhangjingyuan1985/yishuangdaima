package com.sutpc.data.send.bus.gps.tencent.listening;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.CharsetUtil;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;


/**
 * 心跳检测状态处理类.
 */
@Slf4j
@ChannelHandler.Sharable
public class ConnectorIdleStateTrigger extends ChannelInboundHandlerAdapter {

  private static final ByteBuf HEARTBEAT_SEQUENCE = Unpooled
      .unreleasableBuffer(Unpooled.copiedBuffer("Heartbeat",
          CharsetUtil.UTF_8));  //不释放资源，读取后

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) { // 获取idle事件
      IdleStateEvent event = (IdleStateEvent) evt;
      if (event.state() == IdleState.READER_IDLE) {   // 读等待事件
        log.info("在70s内未获取数据，主动关闭链路，时间:{}", new Date());
        System.out.println("主动关闭链路");
        ctx.channel().close();
      } else if (event.state() == IdleState.WRITER_IDLE) { // 等待事件
        byte[] value2 = new byte[3];
        value2[0] = (byte) 0x01;
        value2[1] = (byte) 0x00;
        value2[2] = (byte) 0x00;
        ByteBuf b = Unpooled.unreleasableBuffer(Unpooled.copiedBuffer(value2));
        ctx.writeAndFlush(b);
      }
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }
}
