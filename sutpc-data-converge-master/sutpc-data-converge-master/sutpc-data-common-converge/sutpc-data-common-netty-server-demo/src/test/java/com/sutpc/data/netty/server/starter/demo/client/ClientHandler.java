package com.sutpc.data.netty.server.starter.demo.client;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class ClientHandler extends SimpleChannelInboundHandler<String> {

  @Override
  protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
    System.out.println(ctx.channel().remoteAddress());
    System.out.println("client output : " + msg);

  }

  @Override
  public void channelActive(ChannelHandlerContext ctx) throws Exception {
    log.info("开始发送消息");
    for (int i = 0; i < 10; i++) {
      String str =
          "20190717,113609,H,粤BD57388,114.024150,22.636538,66.0,143,1,1,1,66,2376220,0,0,0,2"
              + "\r\n"
              + "20190717,113752,H,粤BD70350,114.001220,22.688889,34.0,148,1,1,1,34,2060210,0,"
              + "512,0,2"
              + "\r\n"
              + "20190717,113926,H,粤BD81015,113.908424,22.550467,61.0,77,1,1,1,61,2319210,0,0,0,2"
              + "\r\n"
              + "20190717,113921,H,粤B7J9Z5,114.068980,22.614315,42.0,69,1,1,1,42,5646510,0,512,0,2"
              + "\r\n"
              + "20190717,113717,H,粤BD82346,114.043690,22.549286,0.0,82,1,1,1,0,1202590,0,0,0,2"
              + "\r\n"
              + "20190717,113754,H,粤BD83681,114.021800,22.530107,0.0,88,1,1,1,0,2481120,0,0,0,2"
              + "\r\n"
              + "20190717,113539,H,粤BD70347,114.294940,22.722525,13.0,28,1,1,1,13,1732840,0,"
              + "512,0,2"
              + "\r\n"
              + "20190717,113923,H,粤BD70345,114.068840,22.542202,47.0,10,1,1,1,47,2050020,0,"
              + "512,0,2"
              + "\r\n"
              + "20190717,113608,H,粤BD95662,114.074360,22.542913,7.0,48,1,1,1,7,1987270,0,"
              + "512,0,2"
              + "\r\n"
              + "20190717,113754,H,粤BD44071,114.034935,22.545680,11.0,0,1,1,1,11,2135230,"
              + "0,0,0,2"
              + "\r\n";
      ctx.channel().writeAndFlush(str);
    }
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    cause.printStackTrace();
    ctx.close();
  }
}