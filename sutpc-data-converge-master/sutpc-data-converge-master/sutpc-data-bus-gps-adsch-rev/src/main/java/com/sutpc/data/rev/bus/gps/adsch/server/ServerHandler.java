package com.sutpc.data.rev.bus.gps.adsch.server;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class ServerHandler extends ChannelInboundHandlerAdapter {


  @Override
  public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
    String body = (String) msg;
    //System.out.println("Client said:" + body);
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    System.out.println("连接异常.....");
    cause.printStackTrace();
    System.out.println(cause.getCause());
    ctx.close();
  }

  @Override
  public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
    ctx.flush();
  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    System.out.println("连接成功.....");
    /*String response = "hello server!";
    ByteBuf encoded = ctx.alloc().buffer(4 * response.length());
    encoded.writeBytes(response.getBytes());
    ctx.write(encoded);
    ctx.flush();*/
  }

}