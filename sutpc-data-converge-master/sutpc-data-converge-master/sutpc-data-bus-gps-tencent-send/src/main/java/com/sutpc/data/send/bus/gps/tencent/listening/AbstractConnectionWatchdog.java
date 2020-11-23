package com.sutpc.data.send.bus.gps.tencent.listening;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.util.Timeout;
import io.netty.util.Timer;
import io.netty.util.TimerTask;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;


/**
 * 重连检测狗.
 */
@Slf4j
@ChannelHandler.Sharable
public abstract class AbstractConnectionWatchdog extends ChannelInboundHandlerAdapter implements
    TimerTask,
    ChannelHandlerHolder {


  private final Bootstrap bootstrap;
  private final Timer timer;
  private final int port;

  private final String host;

  private volatile boolean reconnect = true;
  private int attempts;

  /**
   * .
   *
   * @param bootstrap 启动类
   * @param timer 定时器
   * @param port 端口
   * @param host IP
   * @param reconnect 是否重连
   */
  public AbstractConnectionWatchdog(Bootstrap bootstrap, Timer timer, int port, String host,
      boolean reconnect) {
    this.bootstrap = bootstrap;
    this.timer = timer;
    this.port = port;
    this.host = host;
    this.reconnect = reconnect;
  }

  /**
   * channel链路每次active的时候，将其连接的次数重新☞ 0.
   */
  @Override
  public void channelActive(ChannelHandlerContext ctx) {
    log.info("当前链路已经激活了，重连尝试次数重新置为0");
    attempts = 0;
    ctx.fireChannelActive();
  }

  @Override
  public void channelInactive(ChannelHandlerContext ctx) {
    log.info("链接关闭");
    log.info("链接关闭，将进行重连");
    int timeout = 3;
    timer.newTimeout(this, timeout, TimeUnit.SECONDS);
    ctx.fireChannelInactive();
  }

  @Override
  public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
    log.info("exceptionCaught method :{}", cause.getMessage());
    //TODO 是否需要出发断开事件,ClientHandler已经发出了
    super.exceptionCaught(ctx, cause);
  }

  /**
   * .
   *
   * @param timeout 超时时长
   */
  @Override
  public void run(Timeout timeout) {

    ChannelFuture future;
    //bootstrap已经初始化好了，只需要将handler填入就可以了
    synchronized (bootstrap) {
      bootstrap.handler(new ChannelInitializer<Channel>() {

        @Override
        protected void initChannel(Channel ch) throws Exception {

          ch.pipeline().addLast(handlers());
        }
      });
      future = bootstrap.connect(host, port);
    }
    //future对象
    future.addListener((ChannelFutureListener) f -> {
      boolean succeed = f.isSuccess();
      //如果重连失败，则调用ChannelInactive方法，再次出发重连事件，一直尝试12次，如果失败则不再重连
      if (!succeed) {
        log.info("重连失败");
        f.channel().pipeline().fireChannelInactive();
      } else {
        log.info("重连成功");
      }
    });

  }

}