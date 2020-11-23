package com.sutpc.data.netty.server.starter.netty.handler;

import com.sutpc.data.netty.server.starter.netty.manager.ConnectionManager;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Sharable
public class ServerHandler extends SimpleChannelInboundHandler<String> {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
    log.info("client msg:{}", msg);
    System.out.println("client msg :" + msg);
    //TODO 添加处理请求的逻辑
    Channel channel = ctx.channel();
    channel.writeAndFlush(msg);
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    log.info("{}+ 上线 ", ctx.channel().remoteAddress());
    System.out.println(ctx.channel().remoteAddress() + "上线");
    //TODO 记录连接的channel
    super.channelActive(ctx);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
    cause.printStackTrace();
    ctx.close();
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    log.info("与客户端断开连接，通道关闭");
    System.out.println("与客户端断开连接，通道关闭");
    ConnectionManager.getInstance().channelInActive(ctx.channel());
    super.channelInactive(ctx);
  }

}