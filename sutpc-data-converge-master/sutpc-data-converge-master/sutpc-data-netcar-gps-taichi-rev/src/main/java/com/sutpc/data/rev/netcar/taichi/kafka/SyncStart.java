package com.sutpc.data.rev.netcar.taichi.kafka;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sutpc.data.rev.netcar.taichi.cache.DataStatistics;
import com.sutpc.data.rev.netcar.taichi.entity.DriverLocationInfo;
import com.sutpc.data.util.KafkaConsumerUtils;
import com.sutpc.data.util.KafkaProducerUtils;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.concurrent.BasicThreadFactory;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SyncStart {

  /**
   * kafka生产.
   */
  private KafkaProducerUtils producer = new KafkaProducerUtils();
  /**
   * 缓存队列.
   */
  private LinkedBlockingQueue<String> queue = new LinkedBlockingQueue<>(1000000);

  /**
   * 数据统计.
   */
  private DataStatistics statistics = DataStatistics.getInstance();

  /**
   * .
   */
  @PostConstruct
  public void start() {
    ExecutorService executorService = new ThreadPoolExecutor(2, 2, 60,
        TimeUnit.SECONDS, new LinkedBlockingQueue<>(1024),
        new BasicThreadFactory.Builder().namingPattern("generate-text-pool-%d").daemon(false)
            .build(), new AbortPolicy());
    executorService.submit(() -> KafkaConsumerUtils.auth(record -> {
      statistics.getDataTotal().incrementAndGet();
      log.debug("offset = {},value = {},timestamp:{},current time:{}", record.offset(),
          record.value(), record.timestamp(), new Date());
      parse(record.value());
    }));
    executorService.submit(() -> {
      while (true) {
        if (!queue.isEmpty()) {
          log.info("size:{}", queue.size());
          int size = queue.size() > 10000 ? 10000 : queue.size();
          for (int i = 0; i < size; i++) {
            statistics.getConsumerDataTotal().incrementAndGet();
            Future<RecordMetadata> send = producer.send(queue.poll());
            //            log.info("future:{}", JSONObject.toJSONString(send));
          }
        }
      }
    });
  }

  /**
   * 将消息转换成字符串.
   *
   * @param msg 消息
   */
  private void parse(String msg) {
    JSONObject jsonObject = JSONArray.parseObject(msg);
    JSONArray array = (JSONArray) jsonObject.get("list");
    statistics.getParseDataTotal().addAndGet(array.size());
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    for (int i = 0; i < array.size(); i++) {
      try {
        JSONObject obj = (JSONObject) array.get(i);
        DriverLocationInfo info = JSONObject.toJavaObject(obj, DriverLocationInfo.class);
        StringBuilder builder = new StringBuilder();
        //时间判断
        Date parse = sdf.parse((String) info.getPositionTime());

        Calendar c = Calendar.getInstance();
        c.setTime(parse);
        Integer year = c.get(Calendar.YEAR);
        if (year < 1972) {
          statistics.getErrorTotal().incrementAndGet();
          continue;
        }
        String month =
            String.valueOf(c.get(Calendar.MONTH) + 1).length() == 1 ? "0" + (c.get(Calendar.MONTH)
                + 1) : (c.get(Calendar.MONTH) + 1) + "";   //获取月份，0表示1月份
        String day = String.valueOf(c.get(Calendar.DAY_OF_MONTH)).length() == 1 ? "0" + c
            .get(Calendar.DAY_OF_MONTH) : c.get(Calendar.DAY_OF_MONTH)
            + "";   //获取月份，0表示1月份 c.get(Calendar.DAY_OF_MONTH);    //获取当前天数
        String date = year + month + day;
        builder.append(date + ",");
        //时分秒
        String hour = String.valueOf(c.get(Calendar.HOUR_OF_DAY)).length() == 1 ? "0" + c
            .get(Calendar.HOUR_OF_DAY) : c.get(Calendar.HOUR_OF_DAY) + "";       //获取当前小时
        String min =
            String.valueOf(c.get(Calendar.MINUTE)).length() == 1 ? "0" + c.get(Calendar.MINUTE)
                : c.get(Calendar.MINUTE) + "";          //获取当前分钟
        String second =
            String.valueOf(c.get(Calendar.SECOND)).length() == 1 ? "0" + c.get(Calendar.SECOND)
                : c.get(Calendar.SECOND) + "";//获取当前秒
        String time = hour + min + second;
        builder.append(time + ",");

        builder.append(info.getPlatformCode() + ",");
        builder.append(info.getVehicleNo() + ",");
        Double lon = Double.parseDouble(String.valueOf(info.getLongitude()));
        Double lat = Double.parseDouble(String.valueOf(info.getLatitude()));
        //GPS坐标为0或者空的数据去掉。
        if (lon == null || lon == 0) {
          statistics.getErrorTotal().incrementAndGet();
          continue;
        }
        if (lat == null || lat == 0) {
          statistics.getErrorTotal().incrementAndGet();
          continue;
        }
        builder.append(info.getLongitude() + ",");
        builder.append(info.getLatitude() + ",");
        builder.append(info.getSpeed() + ",");
        builder.append(info.getDirection() + ",");
        //运营状态 0表示空载，1表示重载
        //1载客，2接单，3空驶，4停运。载客和接单写1，空驶和停运写0.只允许写0和1
        int bizStatus = (int) info.getBizStatus();
        //载客和接单写1
        if (bizStatus == 1 || bizStatus == 2) {
          builder.append(1 + ",");
        }
        if (bizStatus == 3 || bizStatus == 4) {
          builder.append(0 + ",");
        }
        //异常数据可以过滤掉，其他的写1
        builder.append("1,");
        builder.append(info.getDriCertNo() + ",");
        builder.append(info.getLicenseId() + ",");
        builder.append(info.getEncrypt() + ",");
        builder.append(info.getElevation() + ",");
        builder.append(info.getBizStatus() + ",");
        builder.append(info.getPositionType() + ",");
        builder.append(info.getOrderId() + ",");
        builder.deleteCharAt(builder.length() - 1);
        statistics.getAvailableTotal().incrementAndGet();
        queue.add(builder.toString());

      } catch (ParseException e) {
        statistics.getErrorTotal().incrementAndGet();
        log.error("message:{}", e.getMessage(), array.get(i));
      } catch (Exception e) {
        statistics.getErrorTotal().incrementAndGet();
        log.error("message:{}", e.getMessage(), array.get(i));

      }
    }
  }
}