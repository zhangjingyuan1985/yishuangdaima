package com.sutpc.data.send.bus.gps.tencent.cache;

import com.sutpc.data.send.bus.gps.tencent.util.DatabaseUtil;
import com.sutpc.data.util.KafkaConsumerUtils;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;

/**
 * 数据库缓存对象.
 */
@Slf4j
@Data
public class DataCache {

  private List<String> needSendAddress = new ArrayList<>();
  /**
   * 线路名称.
   * Map routeid,routeName  map
   */
  private final Map<String, String> routeNameCache = new ConcurrentHashMap<>();
  /**
   * 子线路名称.
   * Map routeid,routeName map
   */
  private final Map<String, String> subRouteNameCache = new ConcurrentHashMap<>();
  /**
   * 车牌号.
   * Map productid,cardId map
   */
  private final Map<String, String> cardIdCache = new ConcurrentHashMap<>();
  /**
   * 消息队列.
   */
  private final LinkedBlockingQueue<String> queue = new LinkedBlockingQueue(1000000);
  /**
   * 过期时间(毫秒).
   */
  private static final long TIMEOUT = 15 * 60 * 1000;

  private final int threadSize = 6;
  /**
   * 入队列前的总数量.
   */
  private AtomicInteger startTotalCount = new AtomicInteger(0);
  /**
   * 入队列时失败的数量.
   */
  private AtomicInteger startFailCount = new AtomicInteger(0);
  /**
   * 出队列时失败的数量.
   */
  private AtomicInteger endFailCount = new AtomicInteger(0);
  /**
   * 出队列时总数量.
   */
  private AtomicInteger endTotalCount = new AtomicInteger(0);

  /**
   * 连接断开次数.
   */
  private Map<String, AtomicInteger> loss = new ConcurrentHashMap<>();

  /**
   * 总数量.
   */
  private AtomicLong total = new AtomicLong(0);
  /**
   * 无效的数据.
   */
  private AtomicLong lossCount = new AtomicLong(0);

  /**
   * 写出时无效的数据.
   */
  private AtomicLong netCount = new AtomicLong(0);

  private DataCache() {
    cardIdCache();
    routeNameCache();
    subRouteNameCache();
    queueCache();
    //    queueCache1();
  }

  private static class SingletonHolder {

    private static final DataCache INSTANCE = new DataCache();
  }

  public static DataCache getInstance() {
    return SingletonHolder.INSTANCE;
  }

  /**
   * 车牌号缓存.
   */
  private void cardIdCache() {
    log.info("cardIdCache size ：{}", cardIdCache.size());
    String sql = "select productid ,cardid  from BUS_BD t ";
    DatabaseUtil.query(sql, rs -> {
      try {
        cardIdCache.put(rs.getString(1), rs.getString(2));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
    log.info("cardIdCache size ：{}", cardIdCache.size());
  }

  /**
   * routeName缓存.
   */
  private void routeNameCache() {
    log.info("routeNameCache size ：{}", routeNameCache.size());
    String sql = "select t.routeid,t.routename from Route_BD t";
    DatabaseUtil.query(sql, rs -> {
      try {
        routeNameCache.put(rs.getString(1), rs.getString(2));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
    log.info("routeNameCache size ：{}", routeNameCache.size());
  }

  /**
   * routeName缓存.
   */
  private void subRouteNameCache() {
    log.info("subRouteNameCache size ：{}", subRouteNameCache.size());
    String sql = "select subrouteid,t.subroutename from subroute_bd t ";
    DatabaseUtil.query(sql, rs -> {
      try {
        subRouteNameCache.put(rs.getString(1), rs.getString(2));
      } catch (SQLException e) {
        e.printStackTrace();
      }
    });
    log.info("subRouteNameCache size ：{}", subRouteNameCache.size());
  }

  /**
   * 添加消息.
   */
  private void queueCache() {
    ExecutorService executorService = new ThreadPoolExecutor(threadSize, threadSize, 60,
        TimeUnit.SECONDS, new LinkedBlockingQueue<>(1024),
        new BasicThreadFactory.Builder().namingPattern("queue-cache-pool-%d").daemon(false)
            .build(), new AbortPolicy());
    for (int i = 0; i < threadSize; i++) {
      executorService.execute(() -> new KafkaConsumerUtils().receiveSleep(null, records -> {
        total.getAndAdd(records.count());
        startTotalCount.getAndAdd(records.count());
        records.forEach(record -> {

          long currentTime = System.currentTimeMillis();
          if (validTime(record.value())) {
            log.info("offset = {},value = {},timestamp:{},currentTime:{}", record.offset(),
                record.value(), new Date(record.timestamp()), LocalDateTime.now());
            queue.offer(record.value());
          } else {
            log.info("offer 15 min timeout data offset = {},value = {},timestamp:{},currentTime:{}",
                record.offset(), record.value(), new Date(record.timestamp()), LocalDateTime.now());
            startFailCount.getAndIncrement();
            lossCount.getAndIncrement();
          }
          log.info("offer consult time:{}", System.currentTimeMillis() - currentTime);
        });
      }));
    }
  }

  /**
   * 无法连接到数据库时，测试使用.
   */
  private void queueCache1() {
    ExecutorService executorService = new ThreadPoolExecutor(1, 1, 60,
        TimeUnit.SECONDS, new LinkedBlockingQueue<>(1024),
        new BasicThreadFactory.Builder().namingPattern("data-cache1-pool-%d").daemon(false)
            .build(), new AbortPolicy());
    executorService.submit(() -> {
      while (true) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd,HHmmss");
        String str = formatter.format(LocalDateTime.now());
        String test = str + ",H,52559,114.133667,22.642015,0,0,1,1,291,0";
        if (validTime(test.substring(0, 15))) {
          //          log.info(test);
          queue.offer(test);
        }
        Thread.sleep(10);
      }
    });
  }

  /**
   * .
   *
   * @param target 目标数
   * @param p 时间分片
   */
  public static int setEndFailCount(AtomicInteger target, int p) {
    //还在同一分片内
    if (p == getPeriod()) {
      target.getAndIncrement();
    } else { //不同分区重新赋值,重置失败的数量
      p = getPeriod();
      target.set(0);
    }
    return p;
  }

  public void setEndFailCount(int value) {
    endFailCount.set(value);
  }

  public AtomicInteger getStartFailCount() {
    return startFailCount;
  }

  public void setStartFailCount(int value) {
    startFailCount.set(value);
  }


  public AtomicInteger getEndFailCount() {
    return endFailCount;
  }


  public Map<String, AtomicInteger> getLoss() {
    return loss;
  }

  /**
   * 校验时间.
   *
   * @param str 时分秒字符串
   * @return true, 有效的时间，false，无效的时间
   */
  public static boolean validTime(String str) {
    if (str == null || "".equals(str)) {
      return false;
    }
    long score = System.currentTimeMillis();
    long score2;

    Calendar c = Calendar.getInstance();
    try {
      c.setTime(new SimpleDateFormat("yyyyMMdd,HHmmss").parse(str.substring(0, 15)));
    } catch (ParseException e) {
      log.info("message ：{},error:{}", str, e.getMessage());
    }
    score2 = c.getTimeInMillis();
    if (score - score2 < TIMEOUT) {
      return true;
    }
    return false;
  }

  /**
   * 得到当前时间片.
   */
  public static int getPeriod() {
    Calendar instance = Calendar.getInstance();
    int hour = instance.get(Calendar.HOUR_OF_DAY);
    int minute = instance.get(Calendar.MINUTE);
    int second = instance.get(Calendar.SECOND);
    int score = hour * 60 * 60 + minute * 60 + second;
    return score / 700;
  }

  public boolean addNeedSendAddress(String value) {
    return needSendAddress.add(value);
  }

  public List<String> getNeedSendAddress() {
    return needSendAddress;
  }
}
