package com.sutpc.data.rev.vitalvehicle.gps.ctfo.netty;

import com.sutpc.data.util.KafkaProducerUtils;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ServerHandler extends SimpleChannelInboundHandler<String> {

  private AtomicInteger lossConnectCount = new AtomicInteger(0);

  private KafkaProducerUtils producerUtils = new KafkaProducerUtils();


  @Override
  protected void channelRead0(ChannelHandlerContext ctx, String msg) {
    log.info("server msg:{}", msg);
    String[] array = msg.split(",");
    int type = Integer.parseInt(array[array.length - 1]);
    GpsTypeEnum tp = GpsTypeEnum.getByValue(type);
    if (tp == null) {
      return;
    }
    //生产消息
    producerUtils.send(tp.getName(), msg);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    super.channelActive(ctx);
    Channel channel = ctx.channel();
    log.info("remote address ：{}+ connected ", channel.remoteAddress());

  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    super.channelInactive(ctx);
    Channel channel = ctx.channel();
    log.info("remote address ：{}+ inactive", channel.remoteAddress());
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    if (evt instanceof IdleStateEvent) {  // 获取idle事件
      IdleStateEvent event = (IdleStateEvent) evt;
      if (event.state() == IdleState.READER_IDLE) {   // 读等待事件
        lossConnectCount.getAndIncrement();
        if (lossConnectCount.get() >= 3) {  // 3次断开连接
          ctx.channel().close();
        }
      }
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }
}