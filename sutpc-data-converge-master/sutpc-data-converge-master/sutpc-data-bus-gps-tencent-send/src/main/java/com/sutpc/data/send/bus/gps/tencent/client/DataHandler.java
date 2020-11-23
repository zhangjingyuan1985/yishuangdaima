package com.sutpc.data.send.bus.gps.tencent.client;

import com.alibaba.fastjson.JSON;
import com.sutpc.data.send.bus.gps.tencent.cache.ChannelCache;
import com.sutpc.data.send.bus.gps.tencent.cache.DataCache;
import com.sutpc.data.send.bus.gps.tencent.dto.InfoUnit;
import com.sutpc.data.send.bus.gps.tencent.vo.MutilRequest;
import com.sutpc.data.send.bus.gps.tencent.vo.Request;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataHandler {

  /**
   * 线路名称.
   */
  private Map<String, String> routeNameCache = DataCache.getInstance().getRouteNameCache();
  /**
   * 子线路名称.
   */
  private Map<String, String> subRouteNameCache = DataCache.getInstance().getSubRouteNameCache();
  /**
   * 车牌号.
   */
  private Map<String, String> cardIdCache = DataCache.getInstance().getCardIdCache();
  /**
   * 消息队列.
   */
  private LinkedBlockingQueue<String> queue = DataCache.getInstance().getQueue();

  /**
   * channel缓存.
   */
  private ChannelCache channelCache = ChannelCache.getInstance();

  private final int threadSize = 1 << 3;

  /**
   * 队列累积的次数.
   */
  private AtomicInteger cumulativeQuantity = new AtomicInteger(0);
  /**
   * 队列累积临界值.
   */
  private static final double CRITICAL_POINT_THRESHOLD = 400;


  private DataHandler() {
    handler();
  }

  private static class SingletonHolder {

    private static final DataHandler INSTANCE = new DataHandler();
  }

  public static DataHandler getInstance() {
    return DataHandler.SingletonHolder.INSTANCE;
  }

  private void handler() {
    ExecutorService executorService = Executors.newFixedThreadPool(threadSize);
    for (int i = 0; i < threadSize; i++) {
      executorService.submit(() -> {
        while (true) {
          if (!queue.isEmpty()) {
            int queueSize = queue.size();
            log.info("queue size is {}", queueSize);
            if (queueSize < 1000) {
              cumulativeQuantity.getAndIncrement();
              Thread.sleep(50);
            } else {
              cumulativeQuantity.getAndSet(0);
              sendMessage(1000);
            }
            if (cumulativeQuantity.get() >= CRITICAL_POINT_THRESHOLD) {
              log.info("cumulative quantity:{}", cumulativeQuantity.getAndSet(0));
              sendMessage(queueSize);
            }
          } else {
            try {
              Thread.sleep(50);
            } catch (InterruptedException e) {
              log.info("handler sleep method:{}", e.getMessage());
            }
          }
        }
      });
    }
  }

  private List<InfoUnit> toInfoUnit(String str) throws UnsupportedEncodingException {

    String[] msg = str.split(",");
    List<InfoUnit> infoUnits = new ArrayList<>();

    //日期
    infoUnits.add(new InfoUnit(0x01, msg[0].getBytes("GBK").length, msg[0]));

    //时间
    if (msg[1].length() != 6) {
      log.info("长度不对{}", str);
      return null;
    } else if (!DataCache.validTime(str)) {
      log.info("take 15 min timeout:{}", str);
      return null;
    }
    infoUnits.add(new InfoUnit(0x02, msg[1].getBytes("GBK").length, msg[1]));

    //线路名称
    String subRouteName = subRouteNameCache.get(msg[11]);
    String routeName = routeNameCache.get(msg[10]);
    if (subRouteName != null && !subRouteName.equals("")) {
      infoUnits.add(new InfoUnit(0x03, subRouteName.getBytes("GBK").length, subRouteName));
    } else if (routeName != null && !routeName.equals("")) {
      infoUnits.add(new InfoUnit(0x03, routeName.getBytes("GBK").length, routeName));
    } else {
      infoUnits.add(new InfoUnit(0x03, 0, ""));
    }

    //车辆编号
    String carNumber = cardIdCache.get(msg[3]);
    if (carNumber != null && !carNumber.equals("")) {
      infoUnits.add(new InfoUnit(0x04, 32, carNumber));
    } else {
      infoUnits.add(new InfoUnit(0x04, 32, ""));
    }

    //速度
    infoUnits.add(new InfoUnit(0x05, 2, msg[6]));

    //方向
    infoUnits.add(new InfoUnit(0x06, 2, msg[7]));

    //经度
    infoUnits.add(new InfoUnit(0x07, 4, msg[4]));

    //纬度
    infoUnits.add(new InfoUnit(0x08, 4, msg[5]));

    return infoUnits;
  }

  /**
   * 发送消息.
   *
   * @param queueSize 队列的数量
   */
  private void sendMessage(int queueSize) {
    //入队列的总数据量
    DataCache.getInstance().getEndTotalCount().getAndAdd(queueSize);
    MutilRequest mutilRequest = new MutilRequest();
    List<Request> requestList = new ArrayList<>();
    for (int j = 0; j < queueSize; j++) {
      String msg = null;
      try {
        msg = queue.take();
      } catch (InterruptedException e) {
        log.info("sendMessage method 175 line:{}", e.getMessage());
      }
      List<InfoUnit> infoUnits = null;
      try {
        infoUnits = toInfoUnit(msg);
      } catch (UnsupportedEncodingException e) {
        log.info("sendMessage method 181 line:{}", e.getMessage());
      }
      //可能存在无效数据
      if (infoUnits == null || infoUnits.isEmpty()) {
        log.info("无效数据：{}", msg);
        //记录失败的次数
        DataCache.getInstance().getEndFailCount().getAndIncrement();
        DataCache.getInstance().getNetCount().getAndIncrement();
        continue;
      }
      int length = infoUnits.stream().mapToInt(item -> 1 + 1 + item.getLength()).sum();
      Request request = new Request();
      request.setHeader(0xff);
      request.setLength(length);
      request.setInfoUnit(infoUnits);
      request.setEnd(0xEE);
      requestList.add(request);
    }
    mutilRequest.setRequests(requestList);
    channelCache.getChannels().forEach(ch -> {
      InetSocketAddress ipSocket = (InetSocketAddress) ch.remoteAddress();
      String address = ipSocket.getAddress().getHostAddress() + ":" + ipSocket.getPort();

      if (ch.isActive()) {
        ch.writeAndFlush(mutilRequest);
        log.info("write realtime to {},mutilRequest size:{},one of content{}", address,
            mutilRequest.getRequests().size(),
            JSON.toJSONString(mutilRequest.getRequests().get(0)));
      } else {
        log.info("连接已断开:{}", ch.remoteAddress());
        try {
          Thread.sleep(50);
        } catch (InterruptedException e) {
          log.info("sendMessage method227line:{}", e.getMessage());
        }
      }
    });
  }

  private List<String> getNotInlineAddress() {
    List<String> onlineAddress = new ArrayList<>();
    channelCache.getChannels().forEach(ch -> {
      InetSocketAddress ipSocket = (InetSocketAddress) ch.remoteAddress();
      String address = ipSocket.getAddress().getHostAddress() + ":" + ipSocket.getPort();
      onlineAddress.add(address);
    });
    List<String> needOnlineAddress = DataCache.getInstance().getNeedSendAddress();
    List<String> notInlineAddress = new ArrayList<>();
    for (int i = 0; i < needOnlineAddress.size(); i++) {
      String needOnline = needOnlineAddress.get(i);
      boolean isOnline = false;
      for (int j = 0; j < onlineAddress.size(); j++) {
        String online = onlineAddress.get(j);
        if (needOnline.equals(online)) {
          isOnline = true;
          break;
        }
      }
      if (!isOnline) {
        notInlineAddress.add(needOnline);
      }
    }
    return notInlineAddress;
  }
}
