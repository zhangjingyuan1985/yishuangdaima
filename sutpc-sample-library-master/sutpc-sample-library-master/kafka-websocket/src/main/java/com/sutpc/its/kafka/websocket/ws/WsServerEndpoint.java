package com.sutpc.its.kafka.websocket.ws;

import com.alibaba.fastjson.JSON;
import com.sutpc.its.kafka.websocket.dto.HttpResult;
import io.micrometer.core.instrument.util.StringUtils;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @Description .
 * @Author:ShangxiuWu
 * @Date: 17:17 2020/7/23.
 * @Modified By:
 */
@Slf4j
@Component
@ServerEndpoint("/websocket/{userId}")
public class WsServerEndpoint {

  private static CopyOnWriteArraySet<WsServerEndpoint> webSocketSet = new CopyOnWriteArraySet<WsServerEndpoint>();


  /**静态变量，用来记录当前在线连接数。应该把它设计成线程安全的.*/
  private static int onlineCount = 0;
  /**concurrent包的线程安全Map，用来存放每个客户端对应的MyWebSocket对象.*/
  //private static Map<String, WsServerEndpoint> webSocketMap = new ConcurrentHashMap<>();
  /**与某个客户端的连接会话，需要通过它来给客户端发送数据.*/
  private Session session;
  /**接收userId.*/
  private String userId = "";

  /**.
   * 连接建立成功调用的方法*/
  @OnOpen
  public void onOpen(Session session, @PathParam("userId") String userId) {
    this.session = session;
    /*this.userId =
        StringUtils.isEmpty(userId) ? UUID.randomUUID().toString() : userId;
    if (webSocketMap.containsKey(userId)) {
      webSocketMap.remove(userId);
      //加入set中
      webSocketMap.put(userId, this);
    } else {
      //加入set中
      webSocketMap.put(userId, this);
      //在线数加1
      addOnlineCount();
    }*/

    webSocketSet.add(this);

    log.info("用户连接:" + userId + ",当前在线人数为:" + getOnlineCount());

    try {
      sendMessage(JSON.toJSONString(HttpResult.ok("连接成功")));
    } catch (IOException e) {
      log.error("用户:" + userId + ",网络异常!!!!!!");
    }
  }

  /**.
   * 连接关闭调用的方法
   */
  @OnClose
  public void onClose() {
    webSocketSet.remove(this);
    subOnlineCount();
    /*if (webSocketMap.containsKey(userId)) {
      webSocketMap.remove(userId);
      //从set中删除
      subOnlineCount();
    }*/
    log.info("user offline :" + userId + ",online num :" + getOnlineCount());
  }

  /**.
   * 收到客户端消息后调用的方法
   *
   * @param message 客户端发送过来的消息*/
  @OnMessage
  public void onMessage(String message, Session session) {
    log.info("user id:" + userId + ",message:" + message);
  }

  /**.
   *
   * @param session 回话
   * @param error 错误信息
   */
  @OnError
  public void onError(Session session, Throwable error) {
    log.error("user id:" + this.userId + ",reason:" + error.getMessage());
    error.printStackTrace();
  }

  /**.
   * 实现服务器主动推送
   */
  public void sendMessage(String message) throws IOException {
    this.session.getBasicRemote().sendText(message);
  }


  /**.
   * 发送自定义消息
   * */
  /*public static void sendInfo(String message, @PathParam("userId") String userId)
      throws IOException {
    log.info("send to :" + userId + "，message:" + message);
    if (StringUtils.isNotBlank(userId) && webSocketMap.containsKey(userId)) {
      webSocketMap.get(userId).sendMessage(message);
    } else {
      log.error("user :" + userId + ",not online！");
    }
  }*/

  /**.
   * 发送自定义消息
   * */
  public static void sendToAll(String message) {
    log.info("send to all user，message:" + message);
    /*webSocketMap.keySet().forEach(key -> {
      try {
        webSocketMap.get(key).sendMessage(message);
      } catch (IOException e) {
        log.error("消息发送失败：{}", message);
      }
    });*/
    for (WsServerEndpoint item : webSocketSet) {
      try {
        item.sendMessage(message);
      } catch (IOException e) {
        log.error("消息发送失败：{}", message);
      }
    }
  }


  public static synchronized int getOnlineCount() {
    return onlineCount;
  }

  public static synchronized void addOnlineCount() {
    onlineCount++;
  }

  public static synchronized void subOnlineCount() {
    onlineCount--;
  }
}
