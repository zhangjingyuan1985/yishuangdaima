package com.sutpc.data.netty.server.starter.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 请修改描述.
 *
 * @filename : NettyServerProperties
 * @creatangshaofengaofeng
 * @updtangshaofenghaofeng
 * @creat2019-09-19116:1619 16:15
 * @updateTime : 2019-09-19 16:16
 */
@ConfigurationProperties(prefix = "netty.server")
@Data
public class NettyServerProperties {

  private Integer port = 4031;
  private Integer bossThreads = 1;
  private Integer workerThreads = 4;
  private Integer parentOptionSoBacklog = 128;
  private Boolean parentOptionTcpNodelay = true;
  private Integer parentOptionSoLinger = -1;
  private Integer parentOptionConnectTimeoutMills = 5000;
  private Boolean parentOptionSoKeepAlive = true;
  private Integer parentOptionSoRcvBuf = 8192;
  private Integer parentOptionSoSndBuf = 8192;
  private Boolean parentOptionSoReuseAddr = true;
  private Boolean childOptionTcpNodelay = true;
  private Integer childOptionSoLinger = -1;
  private Integer childOptionConnectTimeoutMills = 5000;
  private Boolean childOptionKeepAlive = true;
  private Integer childOptionSoRcvBuf = 8192;
  private Integer childOptionSoSndBuf = 8192;
  private String channelInitializerClass;
}
