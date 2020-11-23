package com.sutpc.data.send.bus.gps.tencent.client;

import com.sutpc.data.send.bus.gps.tencent.cache.ChannelCache;
import com.sutpc.data.send.bus.gps.tencent.cache.DataCache;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<Object> {

  /**
   * 数据处理bean.
   */
  private DataHandler handler = DataHandler.getInstance();
  /**
   * channel缓存.
   */
  private ChannelCache channelCache = ChannelCache.getInstance();

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
    String str = new String((byte[]) msg, Charset.forName("gbk"));
    log.info(str);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    log.info("开始发送消息");
    log.info("active :{} on {}", ctx.channel().remoteAddress(), new Date());
    channelCache.put(ctx.channel());
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    log.info("inactive :{} on {}", ctx.channel().remoteAddress(), new Date());
    InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
    String clientIp = ipSocket.getAddress().getHostAddress();

    if (DataCache.getInstance().getLoss().get(clientIp) == null) {
      DataCache.getInstance().getLoss().put(clientIp, new AtomicInteger(1));
    } else {
      DataCache.getInstance().getLoss().get(clientIp).getAndIncrement();
    }

    channelCache.remove(ctx.channel());
    super.channelInactive(ctx);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    log.error("exceptionCaught method :{} on {} cause {}", ctx.channel().remoteAddress(),
        new Date(), cause.getCause());
    //cause.printStackTrace();
    ctx.close();
  }


}
