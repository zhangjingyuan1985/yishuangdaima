package com.sutpc.data.geomagnetism.rev.handle;

import static com.sutpc.data.geomagnetism.rev.constant.Constant.TYPE_HEIGHT;

import com.sutpc.data.geomagnetism.rev.bean.TcpBean;
import com.sutpc.data.geomagnetism.rev.bean.TrafficData;
import com.sutpc.data.geomagnetism.rev.kafka.PushTrafficDataRunnable;
import com.sutpc.data.netty.server.starter.netty.manager.ConnectionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

/**
 * Description:SpiderServerHandler.
 *
 * @author zhy
 * @date 2019/7/31
 */
@Slf4j
public class SpiderServerHandler extends SimpleChannelInboundHandler<TcpBean> {

  private AtomicInteger lossConnectCount = new AtomicInteger(0);


  @Override
  protected void channelRead0(ChannelHandlerContext ctx, TcpBean msg) {
    log.info("client msg:{}", msg);
    TcpBean response;
    Channel channel = ctx.channel();
    switch (msg.getType()) {
      // 校时
      case TYPE_HEIGHT | 0x04:
        response = TcpBean.timingBack(msg);
        break;
      // 上传定时交通数据
      case TYPE_HEIGHT | 0x01:
        List<TrafficData> trafficDataList = TrafficData.getListFromByteArray(msg.getData());
        PushTrafficDataRunnable.getInstance().addAllContent(trafficDataList);
        response = TcpBean.commonBack(msg, true);
        break;
      default:
        response = TcpBean.commonBack(msg, true);
        break;
    }
    channel.writeAndFlush(response.toBytes());
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    log.info("{}+ 上线 ", ctx.channel().remoteAddress());
    super.channelActive(ctx);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    log.error("channel exception", cause);
    ctx.close();
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    log.info("与客户端断开连接，通道关闭");
    ConnectionManager.getInstance().channelInActive(ctx.channel());
    super.channelInactive(ctx);
  }

  @Override
  public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
    // 获取idle事件
    if (evt instanceof IdleStateEvent) {
      IdleStateEvent event = (IdleStateEvent) evt;
      // 读等待事件
      if (event.state() == IdleState.READER_IDLE) {
        lossConnectCount.getAndIncrement();
        // 3次断开连接
        int maxReadIdleCount = 3;
        log.info("idle事件，第{}次", lossConnectCount.get());
        if (lossConnectCount.get() >= maxReadIdleCount) {
          ConnectionManager.getInstance().channelInActive(ctx.channel());
        }
      }
    } else {
      super.userEventTriggered(ctx, evt);
    }
  }
}