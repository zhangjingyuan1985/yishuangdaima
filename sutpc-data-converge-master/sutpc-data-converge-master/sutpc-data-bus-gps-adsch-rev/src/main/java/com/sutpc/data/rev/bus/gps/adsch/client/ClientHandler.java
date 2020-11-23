package com.sutpc.data.rev.bus.gps.adsch.client;

import com.sutpc.data.rev.bus.gps.adsch.bean.GpsBean;
import com.sutpc.data.rev.bus.gps.adsch.bean.Message;
import com.sutpc.data.rev.bus.gps.adsch.storage.KafkaStorage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientHandler extends ChannelInboundHandlerAdapter {

  private KafkaStorage storage = KafkaStorage.getInstance();

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    log.info("激活时间是：" + new Date());
    log.info("ClientHandler channelActive");
    // 写数据
    //    String tpInfo = "2018-11-13 15:30:00,4.56,6.88,7.69,5.66,4.56,6.88,7.69,5.66,4.56,6.88,"
    //        + "7.69,5.66,4.56,6.88,7.69,5.66";
    //    byte[] msgByte;
    //    ByteBuf msg;
    //
    //    msgByte = (tpInfo + "\r\n" + tpInfo + "\r\n").getBytes(Charset.forName("UTF-8"));
    //    msg = Unpooled.buffer(msgByte.length);
    //    msg.writeBytes(msgByte);
    //    ctx.writeAndFlush(msg);

  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    log.info("停止时间是：" + new Date());
    log.info("HeartBeatClientHandler channelInactive");
  }

  /**
   * 本方法用于处理异常.
   */
  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    // 当出现异常就关闭连接
    cause.printStackTrace();
    log.info("管道发生异常,异常原因如下 : ");
    log.info(cause.getMessage());
    log.info("cause:{}", cause.getCause());
    ctx.close();
  }

  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    if (msg instanceof Message) {
      Message message = (Message) msg;
      GpsBean gpsBean = message.getData();
      // log.info("data:{}", gpsBean.toString());
      storage.getQueue().offer(gpsBean.toString());
    }
  }
}
